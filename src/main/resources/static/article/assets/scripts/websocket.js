import SockJS from "https://cdn.jsdelivr.net/npm/sockjs-client/+esm";
import {Client} from "https://cdn.jsdelivr.net/npm/@stomp/stompjs/+esm";

const $wsContainer = document.getElementById('ws');
const $chatroom = document.forms["chatRoom"];
const $room = $chatroom.querySelector(':scope > .room');
const $wsConnectButton = $chatroom.querySelector('[name="entry"]')

let stompClient = null;
let isConnected = false;

$wsConnectButton.addEventListener('click', () => {
    if (!isConnected) {
        const socket = new SockJS("/article-chat");
        stompClient = new Client({
            webSocketFactory: () => socket
        });
        stompClient.onConnect = () => {
            $chatroom.querySelector('[name="chat"]').disabled = false;
            createSystemMessage('대화방에 입장하였습니다.');
            isConnected = true
            stompClient.subscribe(`/topic/message/${new URL(location.href).searchParams.get('id')}`, (message) => {
                const response = JSON.parse(message.body);
                createUserMessage(response.sender, response.content);
                $wsContainer.querySelector('[name="chat"]').value = '';
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
        $chatroom.querySelector('[name="chat"]').disabled = true;
        createUserMessage('system', '대화방을 퇴장하였습니다.')
        stompClient.deactivate();
        isConnected = false;
    }
});

$chatroom.addEventListener('submit', (e) => {
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
            content: $wsContainer.querySelector('[name="chat"]').value
        })
    });
}

function createUserMessage(messageSender, messageContent) {
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
    $room.append($li);
}

function createSystemMessage(message) {
    // todo: 카톡처럼 대화형식 sender === localstorage userid? 말풍선 오른쪽 왼쪽 나누기
    const $li = document.createElement('li');
    const $sender = document.createElement('span');
    const $content = document.createElement('span');
    $li.classList.add('message', 'system');
    $sender.classList.add('sender');
    $sender.innerText = 'system';
    $content.classList.add('content');
    $content.innerText = message;
    $li.append($sender);
    $li.append($content);
    $room.append($li);
}