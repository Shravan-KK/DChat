import pg from 'pg';
import dotenv from 'dotenv';

dotenv.config();

const client = new pg.Client({
  connectionString: process.env.DATABASE_URL,
});


try {
  await client.connect();
  console.log('Connected to the PostgreSQL database');
} catch (err) {
  console.error('Database connection error:', err.stack);
  process.exit(-1);
}


export const db = {
  query: (text, params) => client.query(text, params),
};
