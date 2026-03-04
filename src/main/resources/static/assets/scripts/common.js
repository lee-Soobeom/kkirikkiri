const $nav = document?.getElementById('top');
const $my = document?.getElementById('my');
const $topNav = document?.getElementById('top');
const $chargeButton = $my?.querySelector('[name="charge"]');
const $chargeCloseButton = $my?.querySelector('[name="chargeClose"]');
const $chargeContainer = $my?.querySelector(':scope > .charge-container');
const $currentLocationButton = $my?.querySelector('[name="location"]');
const $paymentButton = $my?.querySelector('[name="payment"]');
const $totalMessages = $topNav?.querySelector(':scope > .button-container > .bell > .total');
const $messageList = $topNav?.querySelector(':scope > .button-container > .bell > .message-list');
const $myContainer = $my?.querySelector(':scope > .container');

window.geoHandler = {
    geocoder: new kakao.maps.services.Geocoder(),
    addressName: '',
    timestamp: '',
    lat: '',
    lng: '',
    error: 0,
    coordsToAddr: (lng, lat) => {
        return new Promise((resolve, reject) => {
            geoHandler.geocoder.coord2RegionCode(lng, lat, (result, status) => {
                if (status === kakao.maps.services.Status.OK) {
                    for (let i = 0; i < result.length; i++) {
                        if (result[i].region_type === 'B') {
                            resolve(result[i].address_name);
                            return;
                        }
                    }
                    reject('ERROR_ADDRESS_TYPE');
                } else {
                    reject(status);
                }
            });
        })
    },
    addrToCoords: (addr) => {
        return new Promise((resolve, reject) => {
            geoHandler.geocoder.addressSearch(addr, (result, status) => {
                if (status === kakao.maps.services.Status.OK) {
                    resolve(result);
                } else {
                    reject(status);
                }
            });
        })
    },
    getGeoLocation: () => {
        return new Promise((resolve, reject) => {
            if (!navigator.geolocation) {
                reject("FAILURE_GEOLOCATION");
                return;
            }
            navigator.geolocation.getCurrentPosition((position) => {
                geoHandler.lat = position.coords.latitude;
                geoHandler.lng = position.coords.longitude;
                geoHandler.timestamp = new Date(position.timestamp);
                geoHandler.coordsToAddr(position.coords.longitude, position.coords.latitude)
                    .then((address) => {
                        geoHandler.addressName = address;
                        if ($my !== null && $chargeButton === null) {
                            localStorage.setItem("location", JSON.stringify({
                                lat: position.coords.latitude,
                                lng: position.coords.longitude,
                            }));
                            $my.querySelector(':scope > .container > .box.user > .info > .address').innerText = geoHandler.addressName;
                        }
                        resolve();
                    })
                    .catch((error) => {
                        reject(error);
                    });
            }, (error) => {
                switch (error.code) {
                    case 1:
                        console.log("사용자가 위치정보 제공을 거부하였습니다.");
                        geoHandler.error = 1;
                        getIp();
                        reject('ERROR_CODE_1');
                        break;
                    case 2:
                        console.log("사용자의 위치를 알 수 없습니다.");
                        geoHandler.error = 2;
                        getIp();
                        reject('ERROR_CODE_2');
                        break;
                    case 3:
                        console.log("요청 시간 초과");
                        geoHandler.error = 3;
                        getIp();
                        reject('ERROR_CODE_3');
                        break;
                    default:
                        console.log("알 수 없는 에러");
                        getIp();
                        reject('ERROR_UNKNOWN');
                }
            }, {
                enableHighAccuracy: false,
                timeout: 5000,
                maximumAge: 0,
            });
        });
    }
};

if ($my != null) {
    $chargeButton?.addEventListener('click', () => {
        $chargeContainer.show();
    });

    $chargeCloseButton?.addEventListener('click', () => {
        $chargeContainer.hide();
    });

    $currentLocationButton?.addEventListener('click', () => {
        dialogHandler.simpleYesNoModal('위치정보 새로고침', '위치정보를 현위치로 업데이트 하시겠습니까?', [{
            caption: '취소',
            onclick: () => {
            }
        }, {
            caption: '확인',
            onclick: () => {
                geoHandler.getGeoLocation()
                    .then(() => $my.querySelector(':scope > .container > .box.user > .info > .address').innerText = geoHandler.addressName)
                    .catch(err => console.error("Error: " + err));
            }
        }])
    });

    $paymentButton?.addEventListener('click', () => {
        const $checkedCost = Array.from($chargeContainer.querySelectorAll('[name="cost"]')).filter(x => x.checked === true)[0];
        if ($checkedCost instanceof HTMLInputElement) {
            let amount;
            if ($checkedCost.value === "-1") {
                const $whitePaper = $my.querySelector('[name="whitePaper"]');
                if (Number.isNaN(+$whitePaper.value) || +$whitePaper.value > 100000 || +$whitePaper.value < 1000) {
                    dialogHandler.simpleYesModal('경고', '최대 10만원, 최소 1천원의 올바른 금액을 입력하세요', {
                        onclick: () => {
                            $whitePaper.focus();
                            $whitePaper.select();
                        }
                    });
                    return;
                } else {
                    amount = $whitePaper.value;
                }
            } else {
                if (Number.isNaN(+$checkedCost.value)) {
                    dialogHandler.simpleYesModal('경고', '잠시후 다시 시도해 주세요.');
                    return;
                } else {
                    amount = $checkedCost.value;
                }
            }
            window.open(`/charge?amount=${amount}`, 'popup', `width=620px,height=${window.screen.height * 2 / 3},left=20,top=40`)
        } else {
            dialogHandler.simpleYesModal('경고', '충전할 금액을 선택 후 결제해 주세요.');
        }
    });

    $my?.addEventListener('mouseup', (e) => {
        if (!$myContainer.contains(e.target) && !$chargeContainer.contains(e.target)) {
            $nav.querySelector(':scope > .image-wrapper > .input').checked = false;
        }
    });
}

function getIp() {
    fetch('https://api.ipify.org?format=json')
        .then(response => response.json())
        .then(data => {
            getIp2Location(data.ip);
        })
        .catch(error => console.error('Error: ', error));
}

function getIp2Location(ip) {
    fetch(`https://geo.ipify.org/api/v2/country,city?apiKey=at_e1Wp0SXcuGvvcYHPuY0uejqbKBeJj&ipAddress=${ip}`)
        .then(res => res.json())
        .then(data => {
            geoHandler.lat = data.location.lat;
            geoHandler.lng = data.location.lng;
            geoHandler.timestamp = new Date();
            geoHandler.coordsToAddr(data.location.lng, data.location.lat)
                .then((addr) => {
                    if ($my !== null && $chargeButton === null) {
                        $my.querySelector(':scope > .container > .box.user > .info > .address').innerText = addr;
                    }
                })
                .catch(err => console.error("Error: ", err));
            localStorage.setItem("location", JSON.stringify({
                lat: data.location.lat,
                lng: data.location.lng,
            }));
        })
        .catch(err => console.error("Error: ", err));
}

function postDenyMessage(message) {
    const xhr = new XMLHttpRequest();
    const formData = new FormData();
    formData.append("sender", message['receiver']);
    formData.append("senderNickname", message['receiverNickname']);
    formData.append("receiver", message['sender']);
    formData.append("receiverNickname", message['senderNickname']);
    formData.append("articleId", message['articleId']);
    formData.append("usage", "deny");
    xhr.onreadystatechange = () => {
        if (xhr.readyState !== XMLHttpRequest.DONE) {
            return;
        }
        if (xhr.status < 200 || xhr.status >= 400) {
            console.log(`error: ${xhr.status}`);
            return;
        }
    };
    xhr.open('POST', '/message/');
    xhr.send(formData);
}

function postAcceptMessage(message) {
    const xhr = new XMLHttpRequest();
    const formData = new FormData();
    formData.append("sender", message['receiver']);
    formData.append("senderNickname", message['receiverNickname']);
    formData.append("receiver", message['sender']);
    formData.append("receiverNickname", message['senderNickname']);
    formData.append("articleId", message['articleId']);
    formData.append("usage", "accept");
    xhr.onreadystatechange = () => {
        if (xhr.readyState !== XMLHttpRequest.DONE) {
            return;
        }
        if (xhr.status < 200 || xhr.status >= 400) {
            console.log(`error: ${xhr.status}`);
            return;
        }
    };
    xhr.open('POST', '/message/');
    xhr.send(formData);
}

function postConfirmParticipant(message) {
    const xhr = new XMLHttpRequest();
    const formData = new FormData();
    formData.append("articleId", message['articleId']);
    formData.append("participant", message['sender']);
    formData.append("participantNickname", message['senderNickname']);
    xhr.onreadystatechange = () => {
        if (xhr.readyState !== XMLHttpRequest.DONE) {
            return;
        }
        if (xhr.status < 200 || xhr.status >= 400) {
            console.log(`error: ${xhr.status}`);
            return;
        }
        const response = JSON.parse(xhr.responseText);
        switch (response.result) {
            case 'SUCCESS':
                dialogHandler.simpleYesNoModal('알림', '해당 게시글로 이동하시겠습니까?', [{
                    caption: "취소",
                }, {
                    caption: "확인",
                    onclick: () => location.href = `/article/share?id=${message['articleId']}`
                }]);
                break;
            case 'FAILURE':
                setTimeout(() => dialogHandler.simpleYesNoModal('경고', '잠시후 다시 시도해 주세요.'), 1000);
                break;
            default:
        }
    };
    xhr.open('POST', '/participant/add');
    xhr.send(formData);
}

function postCheckedMessage(id) {
    const xhr = new XMLHttpRequest();
    const formData = new FormData();
    formData.append("messageId", id);
    xhr.onreadystatechange = () => {
        if (xhr.readyState !== XMLHttpRequest.DONE) {
            return;
        }
        if (xhr.status < 200 || xhr.status >= 400) {
            console.log(`error: ${xhr.status}`);
            return;
        }
    };
    xhr.open('POST', '/message/checked');
    xhr.send(formData);
}

function getMessages() {
    return setInterval(() => {
        // $messageList.querySelectorAll(':scope > .item').forEach($li => $li.remove());
        const xhr = new XMLHttpRequest();
        xhr.onreadystatechange = () => {
            if (xhr.readyState !== XMLHttpRequest.DONE) {
                return;
            }
            if (xhr.status < 200 || xhr.status >= 400) {
                console.log(`error: ${xhr.status}`);
                return;
            }
            const response = JSON.parse(xhr.responseText);
            if (response.messages.length === 0) {
                $totalMessages.classList.add('hidden');
            } else {
                for (const message of response.messages) {
                    const $li = document.createElement('li');
                    $li.classList.add('item');
                    $li.dataset['usage'] = message['usage'];
                    $li.dataset['articleId'] = message['articleId'];
                    const $titleContainer = document.createElement('div');
                    $titleContainer.classList.add('title-container');
                    const $sender = document.createElement('div');
                    $sender.classList.add('sender');
                    $sender.innerText = message['senderNickname'];
                    const $createdAt = document.createElement('div');
                    $createdAt.classList.add('created-at');
                    $createdAt.innerText = message['timestamp'].split('T').join(' ');
                    const $content = document.createElement('div');
                    $content.classList.add('content');
                    $content.innerText = message['content'];
                    $titleContainer.append($sender, $createdAt);
                    $li.append($titleContainer, $content);
                    $messageList.append($li);
                    $li.addEventListener('click', () => {
                        postCheckedMessage(message['id']);
                        switch ($li.dataset['usage']) {
                            case 'confirm':
                                dialogHandler.simpleYesNoModal('알림', `${message['content']}`, [{
                                    caption: '거절',
                                    onclick: () => postDenyMessage(message)
                                }, {
                                    caption: '수락',
                                    onclick: () => {
                                        postConfirmParticipant(message);
                                        postAcceptMessage(message);
                                    }
                                }]);
                                break;
                            case 'accept':
                                dialogHandler.simpleYesNoModal('알림', `${message['content']}\n해당 게시글로 이동하시겠습니까?`, [{
                                    caption: "취소",
                                }, {
                                    caption: "확인",
                                    onclick: () => location.href = `/article/share?id=${message['articleId']}`
                                }]);
                                break;
                            case 'deny':
                                dialogHandler.simpleYesModal('알림', `${message['content']}`);
                                break;
                            case 'system':
                                dialogHandler.simpleYesNoModal('알림', `${message['content']}\n해당 게시글로 이동하시겠습니까?`, [{
                                        caption: "취소",
                                    }, {
                                        caption: "확인",
                                        onclick: () => location.href = `/article/share?id=${message['articleId']}`
                                    }]);
                                break;
                            default:

                        }
                    });
                }
                $totalMessages.classList.remove('hidden');
                $totalMessages.innerText = response.messages.length;
            }
        };
        xhr.open('GET', '/message/');
        xhr.send();
    }, 5000);
}

if (new URL(location.href).pathname === '/' && $chargeButton === null) {
    geoHandler.getGeoLocation();
}

if ($topNav != null) {
    const messageId = $topNav.querySelector(':scope > .button-container > .bell') === null ? '' : getMessages();

    setTimeout(() => clearInterval(messageId), 11000);
}