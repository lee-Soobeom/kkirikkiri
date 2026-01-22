const container = document.getElementById('map');
const options = {
    center: new kakao.maps.LatLng(33.450701, 126.570667),
    level: 3
};

const map = new kakao.maps.Map(container, options);

// WS
let stompClient = null;

function connect() {
    const socket = new SockJS("/ws");
    stompClient = Stomp.over(socket);

    stompClient.connect({}, () => {
        console.log("WebSocket Connected");

        stompClient.subscribe("/topic/messages", (message) => {
            console.log(JSON.parse(message.body));
        });
    });
}

function sendMessage() {
    stompClient.send(
        "/app/chat",
        {},
        JSON.stringify({
            sender: "수범",
            content: "안녕하세요"
        })
    );
}

