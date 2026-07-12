import express from 'express';
import { getUsers } from '../controllers/userController.js';
import authMiddleware from '../middleware/auth.js';

const router = express.Router();

// @route   GET /users
// @desc    Get all registered users except current user
// @access  Private (requires JWT token)
router.get('/', authMiddleware, getUsers);

export default router;
