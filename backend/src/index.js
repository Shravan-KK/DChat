import express from 'express';
import { createServer } from 'http';
import { Server } from 'socket.io';
import cors from 'cors';
import dotenv from 'dotenv';
import axios from 'axios';
import morgan from 'morgan';
import logger from './utils/logger.js';
import { db } from './config/db.js';
import authRoutes from './routes/authRoutes.js';
import userRoutes from './routes/userRoutes.js';
import messageRoutes from './routes/messageRoutes.js';

dotenv.config();

const app = express();
const httpServer = createServer(app);
const io = new Server(httpServer, {
  cors: {
    origin: "*",
    methods: ["GET", "POST"]
  }
});

// Diagnostic Endpoint (Check if server is reachable)
app.get('/ping', (req, res) => {
  logger.info('Ping received from ' + req.ip);
  res.send('pong');
});

// Middleware
app.use(cors());
app.use(express.json());

// Enhanced Request Logger
app.use((req, res, next) => {
  logger.info(`Incoming Request: ${req.method} ${req.url}`, {
    headers: req.headers,
    body: req.method === 'POST' ? req.body : undefined
  });
  next();
});

app.use(morgan('dev'));

// Routes
app.use('/auth', authRoutes);
app.use('/users', userRoutes);
app.use('/messages', messageRoutes);

// Error Handling Middleware
app.use((err, req, res, next) => {
  console.error('\n!!! SERVER ERROR !!!');
  console.error('Stack:', err.stack);
  console.error('--------------------\n');
  res.status(500).json({ message: 'Internal Server Error', error: err.message });
});

// Socket.io Logic for Real-time Messaging
io.on('connection', (socket) => {
  logger.info(`User Connected: ${socket.id}`);

  // User joins a personal room named after their User ID
  socket.on('join', (userId) => {
    socket.join(userId);
    logger.info(`User ${userId} joined their private room`);
  });

  // Handling sending messages
  socket.on('send_message', async (data) => {
    logger.info(`--- Message Flow Started ---`);
    logger.info(`Payload: ${JSON.stringify(data)}`);
    const { senderId, receiverId, content } = data;

    if (!senderId || !receiverId || !content) {
      logger.error('Invalid message data: missing fields');
      return;
    }

    try {
      // 1. Save to Database
      logger.info(`Saving message to PostgreSQL: from ${senderId} to ${receiverId}`);
      const result = await db.query(
        'INSERT INTO messages (sender_id, receiver_id, content) VALUES ($1, $2, $3) RETURNING *',
        [senderId, receiverId, content]
      );
      const savedMessage = result.rows[0];
      logger.info(`Message saved with ID: ${savedMessage.id}`);

      // 2. Emit to Receiver's private room
      logger.info(`Emitting to receiver room: ${receiverId}`);
      io.to(receiverId).emit('receive_message', savedMessage);

      // 3. Emit back to Sender (for confirmation)
      logger.info(`Emitting confirmation to sender: ${senderId}`);
      socket.emit('message_sent', savedMessage);

      // 4. Handle auto-read if receiver is in the chat screen
      // We check if the receiver has joined the room of the sender (indicating an open chat)
      // For simplicity, let's assume if we emit to receiverId, we can also check if they are "active"
      // Alternatively, the frontend fetch history on return to Home screen handles this already.
      // But let's add a small improvement: fetchUsers call now happens on return to Home screen.

      logger.info(`--- Message Flow Completed ---`);
    } catch (err) {
      logger.error('!!! Message Flow Failed !!!', err);
    }
  });

  socket.on('disconnect', () => {
    logger.info('User disconnected');
  });
});

const PORT = process.env.PORT || 3000;

httpServer.listen(PORT, '0.0.0.0', () => {
  console.log(`Server running on port ${PORT}`);
});
