HTMLElement.VISIBLE = 'data-visible';
/** @return {HTMLElement} */
HTMLElement.prototype.hide = function () {
    this.removeAttribute(HTMLElement.VISIBLE);
    return this;
}
/** @return {HTMLElement} */
HTMLElement.prototype.show = function () {
    this.setAttribute(HTMLElement.VISIBLE, '');
    return this;
}

const dialogHandler = {
    /** @type {HTMLElement} */
    $dialog: document.getElementById('dialog'),
    /**
     * @param {{title?: String, content?: String, buttons?: {caption?: String, onclick?: function(HTMLElement?)}[]}} args
     */
    showModal: (args) => {
        const $modal = document.createElement('div');
        $modal.classList.add('modal');
        const $title = document.createElement('div');
        $title.classList.add('title');
        $title.innerText = args.title;
        const $content = document.createElement('div');
        $content.classList.add('content');
        $content.innerText = args.content;
        const $buttonContainer = document.createElement('div');
        $buttonContainer.classList.add('button-container');
        if (args.buttons != null && args.buttons.length > 0) {
            for (const button of args.buttons) {
                const $button = document.createElement('button');
                $button.classList.add('button');
                $button.setAttribute('type', 'button');
                $button.innerText = button.caption;
                if (typeof button.onclick === 'function') {
                    $button.addEventListener('click', () => button.onclick());
                }
                $buttonContainer.append($button);
            }
        }
        $modal.append($title, $content, $buttonContainer);
        dialogHandler.$dialog.append($modal);
        setTimeout(() => dialogHandler.$dialog.show(), 100);
    },
    /**
     * @param {String} title
     * @param {String} content
     * @param {{caption?: string, onclick?: function(HTMLElement?)||undefined}} args
     */
    simpleYesModal: (title, content, args = {caption: "확인", onclick: undefined}) => {
        dialogHandler.showModal({
            title: title,
            content: content,
            buttons: [{
                caption: args?.caption ?? "확인",
                onclick: () => {
                    dialogHandler.$dialog.hide();
                    if (typeof args?.onclick === 'function') {
                        args?.onclick();
                    }
                    setTimeout(() => dialogHandler.$dialog.querySelector(':scope > .modal').remove(), 1000);
                }
            }],
        });
    },
    simpleYesNoModal: (title, content, args = [{
        caption: "취소", onclick: undefined
    }, {
        caption: "확인", onclick: undefined
    }]) => {
        dialogHandler.showModal({
            title: title,
            content: content,
            buttons: [{
                caption: args[0]?.caption ?? "취소",
                onclick: () => {
                    dialogHandler.$dialog.hide();
                    if (typeof args[0]?.onclick === 'function') {
                        args[0]?.onclick();
                    }
                    setTimeout(() => dialogHandler.$dialog.querySelector(':scope > .modal').remove(), 1000);
                }
            }, {
                caption: args[1]?.caption ?? "확인",
                onclick: () => {
                    dialogHandler.$dialog.hide();
                    if (typeof args[1]?.onclick === 'function') {
                        args[1]?.onclick();
                    }
                    setTimeout(() => dialogHandler.$dialog.querySelector(':scope > .modal').remove(), 1000);
                }
            }],
        });
    },
}

const geoHandler = {
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
                        if (result[i].region_type === 'H') {
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
    getGeoLocation: () => {
        return new Promise((resolve, reject) => {
            if (!navigator.geolocation) {
                reject("FAILURE_GEOLOCATION");
                return;
            }
            navigator.geolocation.getCurrentPosition((position) => {
                geoHandler.coordsToAddr(position.coords.longitude, position.coords.latitude)
                    .then((address) => {
                        geoHandler.addressName = address;
                        geoHandler.lat = position.coords.latitude;
                        geoHandler.lng = position.coords.longitude;
                        geoHandler.timestamp = new Date(position.timestamp);
                        resolve();
                    })
                    .catch((error) => {
                        dialogHandler.simpleYesModal('경고', `오류가 발생했습니다. ${error}`);
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

// todo: getIp data 저장하기
function getIp() {
    fetch('https://api.ipify.org?format=json')
        .then(response => response.json())
        .then(data => {
            console.log(data.ip);
            getIp2Location(data.ip);
        })
        .catch(error => console.error('Error: ', error));
}

function getIp2Location(ip) {
    fetch(`https://api.ip2location.io/?key=14867727026084AC48FCEE63022CE8E3&ip=${ip}&format=json&lang=ko`)
        .then((response) => response.json())
        .then(data => console.log(data))
        .catch(error => console.error('Error: ', error));
}

geoHandler.getGeoLocation();

// setInterval(() => {
//     const xhr = new XMLHttpRequest();
//     xhr.onreadystatechange = () => {
//         if (xhr.readyState !== XMLHttpRequest.DONE) {
//             return;
//         }
//         if (xhr.status < 200 || xhr.status >= 400) {
//             dialogHandler.simpleYesModal('경고', '요청에 실패하였습니다. error: ' + `${xhr.status}`);
//             return;
//         }
//         const response = JSON.parse(xhr.responseText);
//         console.log(response.result);
//     };
//     xhr.open('GET', '/message/');
//     xhr.send();
// }, 10000);