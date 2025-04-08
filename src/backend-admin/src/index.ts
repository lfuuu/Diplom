import "reflect-metadata";
import { AppDataSource } from "./data-source";
import { Trunk } from "./entities/Trunk";
import { TrunkRepository } from "./repositories/TrunkRepository";

async function bootstrap() {
  await AppDataSource.initialize();
  
  const trunkRepo = AppDataSource
    .getRepository(Trunk)
    .extend(TrunkRepository.prototype) as TrunkRepository;
  
  // Работа с репозиторием
  await trunkRepo.createTrunk({
    trunkName: "test",
    authByNumber: true
  });
  
  const trunks = await trunkRepo.find();
  console.log(trunks);
  
  await AppDataSource.destroy();
}

bootstrap().catch(console.error);