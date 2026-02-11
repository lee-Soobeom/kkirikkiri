import SockJS from "https://cdn.jsdelivr.net/npm/sockjs-client/+esm";
import {Client} from "https://cdn.jsdelivr.net/npm/@stomp/stompjs/+esm";

const $wsContainer = document.getElementById('ws');
const $my = document.getElementById('my');
const $myNickname = $my.querySelector(':scope > .container > .box.user > .info > .nickname');
const $chatroom = document.forms["chatRoom"];
const $room = $chatroom.querySelector(':scope > .room');
const $wsConnectButton = $chatroom.querySelector('[name="entry"]');


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
            createMessage('system', `${$myNickname.innerText} 님이 대화방에 입장하였습니다.`);
            $wsConnectButton.innerText = '대화퇴장';
            isConnected = true
            stompClient.subscribe(`/topic/message/${new URL(location.href).searchParams.get('id')}`, (message) => {
                const response = JSON.parse(message.body);
                createMessage(response.sender, response.content);
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
        createMessage('system', `${$myNickname.innerText} 님이 대화방을 퇴장하였습니다.`);
        $wsConnectButton.innerText = '대화입장';
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
        destination: `/app/chat/${new URL(location.href).searchParams.get('id')}`,
        body: JSON.stringify({
            sender: $myNickname.innerText,
            content: $wsContainer.querySelector('[name="chat"]').value
        }),
    });
}

function createMessage(messageSender, messageContent) {
    const $li = document.createElement('li');
    const $sender = document.createElement('span');
    const $content = document.createElement('span');
    $li.classList.add('message');
    if (messageSender === 'system') {
        $li.classList.add('system');
    }
    if (messageSender === $myNickname.innerText) {
        $li.classList.add('self');
    }
    $sender.classList.add('sender');
    $sender.innerText = messageSender;
    $content.classList.add('content');
    $content.innerText = messageContent;
    $li.append($sender);
    $li.append($content);
    $room.append($li);
    $room.scrollTop = $room.scrollHeight;
}
