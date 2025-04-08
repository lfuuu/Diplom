import { Express } from 'express'; // Добавляем импорт типа
import { AppDataSource } from "./data-source"
import { serve, setup } from 'swagger-ui-express';
import expressJSDocSwagger from 'express-jsdoc-swagger';

export async function setupSwagger(app: Express) { // Указываем тип параметра
    const connection = AppDataSource;

    // Генерация схем из TypeORM метаданных
    const schemas = connection.entityMetadatas.reduce((acc, entity) => {
        const properties = entity.columns.reduce((props, column) => ({
            ...props,
            [column.propertyName]: {
                type: column.type instanceof Function ? column.type.name : column.type,
                nullable: column.isNullable,
                description: column.comment || '',
            }
        }), {});

        return {
            ...acc,
            [entity.name]: {
                type: 'object',
                properties,
                required: entity.columns
                    .filter(col => !col.isNullable)
                    .map(col => col.propertyName)
            }
        };
    }, {});

    const options = {
        info: {
            title: 'API Documentation',
            version: '1.0.0'
        },
        baseDir: __dirname,
        filesPattern: './**/*.ts',
        swaggerUIPath: '/api-docs',
        exposeSwaggerUI: true,
        swaggerOptions: {
            components: { schemas }
        }
    };

    // Инициализация Swagger
  //  expressJSDocSwagger(app)(options);
    app.use('/api-docs', serve, setup(undefined, options));
    
}