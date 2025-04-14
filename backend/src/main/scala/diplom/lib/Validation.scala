package sorm.lib

// https://blog.ssanj.net/posts/2019-08-18-using-validated-for-error-accumulation-in-scala-with-cats.html

import scala.util.matching.Regex

import cats.data.Validated.{ Invalid, Valid }
import cats.data.ValidatedNel
import cats.implicits._

object Validation {

  // field must be present
  trait Required[A]       extends (A => Boolean)
  // Соответствие паттерну
  trait PatternMatched[A] extends ((A, Regex) => Boolean)
  // minimum value
  trait Minimum[A]        extends ((A, Double) => Boolean) // for numerical fields
  trait MinimumAbs[A]     extends ((A, Double) => Boolean) // for numerical fields

  // TC instances
  implicit val patternMatchedString: PatternMatched[String] = (s, r) => r.pattern.matcher(s).matches

  implicit val patternMatchedOptionString: PatternMatched[Option[String]] = (s, r) => r.pattern.matcher(s.getOrElse("")).matches
  implicit val requiredString: Required[String]                           = _.nonEmpty
  implicit val requiredInt: Required[Option[Int]]                         = _.isDefined
  implicit val requiredLong: Required[Option[Long]]                       = _.isDefined

  implicit val requiredOptionString: Required[Option[String]] =
    _.map(_.nonEmpty).getOrElse(false)

  implicit val minimumOptionInt: Minimum[Option[Int]] = (n, min) => n.map(_ >= min).getOrElse(false)
  implicit val minimumInt: Minimum[Int]               = _ >= _
  implicit val minimumDouble: Minimum[Double]         = _ >= _
  implicit val minimumIntAbs: MinimumAbs[Int]         = Math.abs(_) >= _
  implicit val minimumDoubleAbs: MinimumAbs[Double]   = Math.abs(_) >= _

  // usage
  def required[A](value: A)(implicit req: Required[A]): Boolean = req(value)

  def minimum[A](value: A, threshold: Double)(implicit min: Minimum[A]): Boolean =
    min(value, threshold)

  def minimumAbs[A](value: A, threshold: Double)(implicit min: MinimumAbs[A]): Boolean =
    min(value, threshold)
  def matched[A](value: A, regex: Regex)(implicit req: PatternMatched[A]): Boolean     = req(value, regex)

  // Validated

  type ValidationResult[A] = ValidatedNel[ValidationFailure, A]

  // validation failures
  trait ValidationFailure {
    def errorMessage: String
  }

  case class EmptyField(fieldName: String) extends ValidationFailure {
    override def errorMessage = s"$fieldName is empty"
  }

  case class NegativeValue(fieldName: String) extends ValidationFailure {
    override def errorMessage = s"$fieldName is negative"
  }

  case class BelowMinimumValue(fieldName: String, min: Double) extends ValidationFailure {

    override def errorMessage =
      s"$fieldName is below the minimum threshold $min"
  }

  case class WrongPatternMatchedField(fieldName: String, v: String, p: Regex) extends ValidationFailure {
    override def errorMessage = s"в поле $fieldName значение $v не сответвует регулярному выражению ${p.toString}"
  }

  // "main" API
  def validateMinimum[A: Minimum](value: A, threshold: Double, fieldName: String): ValidationResult[A] =
    if (minimum(value, threshold)) value.validNel
    else if (threshold == 0) NegativeValue(fieldName).invalidNel
    else BelowMinimumValue(fieldName, threshold).invalidNel

  def validateMinimumAbs[A: MinimumAbs](value: A, threshold: Double, fieldName: String): ValidationResult[A] =
    if (minimumAbs(value, threshold)) value.validNel
    else BelowMinimumValue(fieldName, threshold).invalidNel

  def validateRequired[A, B: Required](field: A, value: B, errmsg: String): ValidationResult[A] =
    if (required(value)) field.validNel
    else EmptyField(errmsg).invalidNel

  def validatePatternMatched[A, B: PatternMatched](
    field: A,
    value: B,
    regexp: Regex,
    errmsg: String
  ): ValidationResult[A] =
    if (matched(value, regexp)) field.validNel
    else WrongPatternMatchedField(errmsg, value.toString, regexp).invalidNel

  // general TC for requests
  trait Validator[A] {
    def validate(value: A): ValidationResult[A]
  }

  def validateEntity[A](value: A)(implicit validator: Validator[A]): ValidationResult[A] =
    validator.validate(value)

  def validatorSeq[A](implicit validator: Validator[A]): Validation.Validator[Seq[A]] =
    new Validator[Seq[A]] {

      override def validate(
        request: Seq[A]
      ): ValidationResult[Seq[A]] =
        request
          .map(a => validateEntity(a))
          .foldLeft(Seq[A]().validNel[ValidationFailure]) { (seq, item) =>
            (seq, item) match {
              case (Valid(seq), Valid(item))        => Valid(seq.appendK(item))
              case (i @ Invalid(_), Valid(_))       => i
              case (Valid(_), i @ Invalid(_))       => Invalid(i.e)
              case (s @ Invalid(_), i @ Invalid(_)) => Invalid(s.e.appendList(i.e.toList))
            }
          }
    }

}
