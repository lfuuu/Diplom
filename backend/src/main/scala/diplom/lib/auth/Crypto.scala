package com.mcn.diplom.lib.auth

import java.security.{ MessageDigest, SecureRandom }
import java.util.Base64
import javax.crypto.spec.{ IvParameterSpec, PBEKeySpec, SecretKeySpec }
import javax.crypto.{ Cipher, SecretKeyFactory }

import cats.effect.Sync
import cats.syntax.all._
import com.mcn.diplom.config.types.PasswordSalt
import com.mcn.diplom.domain.Auth._
import org.typelevel.log4cats.Logger

trait Crypto {
  def encrypt(value: Password): EncryptedPassword
  def decrypt(value: EncryptedPassword): Password
  def hashMd5(value: Password): EncryptedPassword
}

object Crypto {

  // https://java-online.ru/javax-crypto.xhtml

  def make[F[_]: Sync: Logger](passwordSalt: PasswordSalt): F[Crypto] =
    Sync[F]
      .delay {
        val random   = new SecureRandom()
        val ivBytes  = new Array[Byte](16)
        random.nextBytes(ivBytes)
        val iv       = new IvParameterSpec(ivBytes);
        val salt     = passwordSalt.secret.value.getBytes("UTF-8")
        val keySpec  = new PBEKeySpec("password".toCharArray(), salt, 65536, 256)
        val factory  = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA1")
        val bytes    = factory.generateSecret(keySpec).getEncoded
        val sKeySpec = new SecretKeySpec(bytes, "AES")
        val eCipher  =
          EncryptCipher(
            Cipher.getInstance("AES/ECB/PKCS5Padding")
          )                                               // AES/CBC/PKCS5Padding - в этом режиме после переинициализации с iv по другому шифрует каждый раз.
        eCipher.value.init(Cipher.ENCRYPT_MODE, sKeySpec) // , iv
        val dCipher = DecryptCipher(Cipher.getInstance("AES/ECB/PKCS5Padding"))
        dCipher.value.init(Cipher.DECRYPT_MODE, sKeySpec) // , iv  - тут убран третий параметр

        (eCipher, dCipher)
      }
      .map {
        case (ec, dc) =>
          new Crypto {

            def encrypt(password: Password): EncryptedPassword = {
              val base64 = Base64.getEncoder()
              val bytes  = password.value.getBytes("UTF-8")
              val result = new String(base64.encode(ec.value.doFinal(bytes)), "UTF-8")
              EncryptedPassword(result)
            }

            def decrypt(password: EncryptedPassword): Password = {
              val base64 = Base64.getDecoder()
              val bytes  = base64.decode(password.value.getBytes("UTF-8"))
              val result = new String(dc.value.doFinal(bytes), "UTF-8")
              Password(result)
            }

            def hashMd5(password: Password): EncryptedPassword = {
              val md            = MessageDigest.getInstance("MD5")
              val messageDigest = md.digest(password.value.getBytes()).map("%02X".format(_)).mkString;
              EncryptedPassword(messageDigest)
            }
          }
      }

}
