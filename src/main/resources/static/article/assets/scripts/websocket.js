import SockJS from "https://cdn.jsdelivr.net/npm/sockjs-client/+esm";
import { Client } from "https://cdn.jsdelivr.net/npm/@stomp/stompjs/+esm";

const wsContainer = document.getElementById('ws');
const chatRoom = document.forms["chatRoom"];
const chat = chatRoom.querySelector(':scope > .room');
const wsConnectButton = chatRoom.querySelector('[name="entry"]')

let stompClient = null;
let isConnected = false;

wsConnectButton.addEventListener('click', () => {

    if (!isConnected) {
        const socket = new SockJS("/article-chat");
        stompClient = new Client({
            webSocketFactory: () => socket
        });

        stompClient.onConnect = () => {
            console.log("WebSocket Connected");
            isConnected = true
            stompClient.subscribe(`/topic/message/${1}`, (message) => {
                const response = JSON.parse(message.body);
                createMessage(response.sender, response.content);
                wsContainer.querySelector('[name="chat"]').value = '';
            });
        };
        stompClient.onWebSocketError = (error) => {
            console.error('Error with websocket', error);
        };
        stompClient.onStompError = (frame) => {
            console.error('Broker reported error: ' + frame.headers['message']);
            console.error('Additional details: ' + frame.body);
        };

        stompClient.activate();
    } else { // 채팅창 끌때
        console.log("WebSocket Disconnected");
        stompClient.deactivate();
        isConnected = false;
    }
});

chatRoom.addEventListener('submit', (e) => {
    e.preventDefault();
    sendMessage();
})

function sendMessage() {
    if (!stompClient || !stompClient.connected) {
        alert("대화에 입장해 주세요!");
        return;
    }

    stompClient.publish({
        destination: `/app/chat/${1}`,
        body: JSON.stringify({
            sender: "me",
            content: wsContainer.querySelector('[name="chat"]').value
        })
    });
}

function createMessage(messageSender, messageContent) {
    // todo: 카톡처럼 대화형식 sender === localstorage userid? 말풍선 오른쪽 왼쪽 나누기
    const $li = document.createElement('li');
    const $sender = document.createElement('span');
    const $content = document.createElement('span');
    $li.classList.add('message');
    $sender.classList.add('sender');
    $sender.innerText = messageSender;
    $content.classList.add('content');
    $content.innerText = messageContent;
    $li.append($sender);
    $li.append($content);
    chat.append($li);
}