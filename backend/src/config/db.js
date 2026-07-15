import pg from 'pg';
import dotenv from 'dotenv';

import logger from '../utils/logger.js';

dotenv.config();

const client = new pg.Client({
  connectionString: process.env.DATABASE_URL,
});


try {
  await client.connect();
  logger.info('Connected to the PostgreSQL database');
} catch (err) {
  logger.error('Database connection error:', { error: err.stack });
  process.exit(-1);
}


export const db = {
  query: (text, params) => client.query(text, params),
};
