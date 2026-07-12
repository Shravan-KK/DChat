import { db } from '../config/db.js';
import axios from 'axios';

export const getUsers = async (req, res) => {
  try {
    const result = await db.query(
      'SELECT id, username, email FROM users WHERE id != $1',
      [req.user]
    );

    let users = result.rows;

    res.json(users);
  } catch (err) {
    console.error('Error fetching users:', err.message);
    res.status(500).send('Server error');
  }
};
