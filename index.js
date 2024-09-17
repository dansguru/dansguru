const express = require('express');
const bodyParser = require('body-parser');
const admin = require('firebase-admin');

admin.initializeApp();
const db = admin.firestore();
const app = express();

app.use(bodyParser.json());

app.post('/mpesa-callback', async (req, res) => {
  const callbackData = req.body;
  console.log('M-Pesa Callback Data:', JSON.stringify(callbackData));

  if (!callbackData || !callbackData.Body || !callbackData.Body.stkCallback) {
    console.error('Invalid callback data format');
    return res.status(400).send('Invalid data format');
  }

  const stkCallback = callbackData.Body.stkCallback;

  // Log callback data to Firestore
  await db.collection('mpesaCallbacks').add(stkCallback);

  const { ResultCode, ResultDesc, CallbackMetadata } = stkCallback;

  if (ResultCode === 0) {
    console.log('Transaction successful:', ResultDesc);

    // Handle successful transaction
    // Update Firestore or other logic
  } else {
    console.log('Transaction failed:', ResultDesc);
    // Handle transaction failure
  }

  res.status(200).send('Callback received and processed successfully.');
});

const PORT = process.env.PORT || 8080;
app.listen(PORT, () => {
  console.log(`Server is running on port ${PORT}`);
});
