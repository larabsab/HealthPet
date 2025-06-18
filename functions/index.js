const {onDocumentCreated} = require("firebase-functions/v2/firestore");
const {initializeApp} = require("firebase-admin/app");
const {getFirestore} = require("firebase-admin/firestore");
const {getMessaging} = require("firebase-admin/messaging");

initializeApp();

exports.sendStatusUpdateNotification = onDocumentCreated(
    "pets/{tutorEmail}/{petId}/status/{statusId}",
    async (event) => {
        const statusData = event.data.data();
        const tutorEmail = event.params.tutorEmail;

        // Busca o token do tutor
        const tutorDoc = await getFirestore().collection("tutors").doc(tutorEmail).get();
        const fcmToken = tutorDoc.data().fcmToken;

        if (!fcmToken) return;

        const message = {
            notification: {
                title: "Nova atualização de status do seu pet!",
                body: statusData.message || "Seu pet recebeu uma nova atualização.",
            },
            token: fcmToken,
        };

        await getMessaging().send(message);
    }
);
