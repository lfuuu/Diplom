import { Entity, PrimaryGeneratedColumn, Column } from "typeorm";

@Entity({ schema: "auth" })
export class Trunk {
  @PrimaryGeneratedColumn()
  id!: number;

  @Column({ name: "trunk_name", nullable: true })
  trunkName!: string;

  @Column({ name: "auth_by_number", default: false })
  authByNumber!: boolean;
}