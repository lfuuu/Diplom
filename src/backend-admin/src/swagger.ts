import { AppDataSource } from "./data-source"
import { serve, setup } from 'swagger-ui-express';
import { Express } from 'express';

export async function setupSwagger(app: Express) {
    const connection = AppDataSource;
    const entityMetadatas = connection.entityMetadatas;

    const schemas = entityMetadatas.reduce((acc, entity) => {
        const properties = entity.columns.reduce((acc, column) => {
            const type = column.type instanceof Function ? column.type.name : column.type;
            acc[column.propertyName] = {
                type,
                nullable: column.isNullable,
                description: column.comment || '',
            };
            return acc;
        }, {});

        acc[entity.name] = {
            type: 'object',
            properties,
        };

        return acc;
    }, {});

    const options = {
        info: {
            title: 'API Documentation',
            version: '1.0.0',
            description: 'Automatically generated from TypeORM entities',
        },
        baseDir: __dirname,
        filesPattern: ['**/*.ts'],
        swaggerUIPath: '/api-docs',
        exposeSwaggerUI: true,
        exposeApiDocs: false,
        swaggerOptions: {
            components: {
                schemas
            }
        }
    };


}