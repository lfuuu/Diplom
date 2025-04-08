import { Entity, PrimaryGeneratedColumn, Column } from "typeorm"
import { ApiProperty } from '@nestjs/swagger';

@Entity()
export class User {

    @PrimaryGeneratedColumn()
    @ApiProperty({ example: 1, description: 'Unique identifier' })
    id: number

    @Column()
    @ApiProperty({ example: 'John Doe', description: 'firstName' })
    firstName: string

    @Column()
    @ApiProperty({ example: 'John Doe', description: 'lastName' })
    lastName: string

    @Column()
    @ApiProperty({ example: '11', description: 'age' })
    age: number

}
