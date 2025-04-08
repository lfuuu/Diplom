import { DataSource } from "typeorm";
import { Trunk } from "./entities/Trunk";
import { TrunkRepository } from "./repositories/TrunkRepository";

export const AppDataSource = new DataSource({
  type: "postgres",
  host: "db",
  port: 5432,
  username: "postgres",
  password: "123",
  database: "nispd",
  schema: "auth",
  synchronize: false,
  logging: true,
  entities: [Trunk],
});