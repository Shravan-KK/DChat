import express from 'express';
import { getChatHistory, markAsRead } from '../controllers/messageController.js';
import authMiddleware from '../middleware/auth.js';

const router = express.Router();

// @route   GET /messages/:receiverId
// @desc    Get chat history between current user and another user
// @access  Private
router.get('/:receiverId', authMiddleware, getChatHistory);

// @route   POST /messages/read/:senderId
// @desc    Mark all messages from a specific sender as read
// @access  Private
router.post('/read/:senderId', authMiddleware, markAsRead);

export default router;
