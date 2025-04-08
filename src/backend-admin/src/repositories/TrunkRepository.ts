import { Repository } from "typeorm";
import { Trunk } from "../entities/Trunk";

export class TrunkRepository extends Repository<Trunk> {
  async findByName(trunkName: string): Promise<Trunk | null> {
    return this.findOne({ where: { trunkName } });
  }
  
  async createTrunk(trunkData: Partial<Trunk>): Promise<Trunk> {
    const trunk = this.create(trunkData);
    return this.save(trunk);
  }
}