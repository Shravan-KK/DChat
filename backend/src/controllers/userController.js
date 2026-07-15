import { db } from '../config/db.js';
import axios from 'axios';

export const getUsers = async (req, res) => {
  try {
    const result = await db.query(
      `SELECT u.id, u.username, u.email,
              MAX(m.timestamp) as last_message_time,
              COUNT(m.id) FILTER (WHERE m.is_read = false AND m.receiver_id = $1) as unread_count
       FROM users u
       LEFT JOIN messages m ON (u.id = m.sender_id AND m.receiver_id = $1)
                            OR (u.id = m.receiver_id AND m.sender_id = $1)
       WHERE u.id != $1
       GROUP BY u.id
       ORDER BY last_message_time DESC NULLS LAST`,
      [req.user]
    );

    let users = result.rows;

    res.json(users);
  } catch (err) {
    console.error('Error fetching users:', err.message);
    res.status(500).send('Server error');
  }
};
