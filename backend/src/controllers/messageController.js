import { db } from '../config/db.js';

export const getChatHistory = async (req, res) => {
  const { receiverId } = req.params;
  const senderId = req.user; // From authMiddleware

  try {
    const result = await db.query(
      `SELECT * FROM messages
       WHERE (sender_id = $1 AND receiver_id = $2)
       OR (sender_id = $2 AND receiver_id = $1)
       ORDER BY timestamp ASC`,
      [senderId, receiverId]
    );

    // Mark messages as read when history is fetched
    await db.query(
      'UPDATE messages SET is_read = true WHERE receiver_id = $1 AND sender_id = $2 AND is_read = false',
      [senderId, receiverId]
    );

    res.json(result.rows);
  } catch (err) {
    console.error('Error fetching chat history:', err.message);
    res.status(500).send('Server error');
  }
};

export const markAsRead = async (req, res) => {
  const { senderId } = req.params;
  const receiverId = req.user;

  try {
    await db.query(
      'UPDATE messages SET is_read = true WHERE receiver_id = $1 AND sender_id = $2 AND is_read = false',
      [receiverId, senderId]
    );
    res.json({ message: 'Messages marked as read' });
  } catch (err) {
    console.error('Error marking messages as read:', err.message);
    res.status(500).send('Server error');
  }
};
