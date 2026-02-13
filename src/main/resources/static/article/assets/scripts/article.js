const $main = document.getElementById('main');
const $menuBox = $main.querySelector(':scope > .container > .context-container > .content-container > .order-card > .menu-container > .box.menu');
const $menu = $main.querySelector(':scope > .container > .context-container > .content-container > .order-card > .menu-container > .box.menu > .caption');
const $restaurant = $main.querySelector(':scope > .container > .context-container > .content-container > .order-card > .order-container > .box.restaurant > .caption');
const $participateButton = $main.querySelector('[name="participate"]');
const $payButton = $main.querySelector('[name="pay"]');
// kakao map
const container = document.getElementById('map');
const options = {
    center: new kakao.maps.LatLng(33.450701, 126.570667),
    level: 3
};
const markerImage = new kakao.maps.MarkerImage(
    `/assets/images/main/main.menu.${$menu.innerText}.png`,
    new kakao.maps.Size(120, 120),
    {
        offset: new kakao.maps.Point(60, 57)
    }
);
const map = new kakao.maps.Map(container, options);
const ps = new kakao.maps.services.Places();

if ($participateButton !== null) {
    $participateButton.addEventListener('click', () => {
        const xhr = new XMLHttpRequest();
        const formData = new FormData();
        formData.append("articleId", new URL(location.href).searchParams.get('id'));
        formData.append("usage", "confirm");
        xhr.onreadystatechange = () => {
            if (xhr.readyState !== XMLHttpRequest.DONE) {
                return;
            }
            if (xhr.status < 200 || xhr.status >= 400) {
                dialogHandler.simpleYesModal('오류', `요청을 전송하는 도중 오류가 발생했습니다. 잠시 후 다시 시도해 주세요. (${xhr.status})`);
                return;
            }
            const response = JSON.parse(xhr.responseText);
            switch (response.result) {
                case 'SUCCESS':
                    dialogHandler.simpleYesModal('알림', '공구 게시글 방장에게 참여 신청 하였습니다.\n방장의 수락까지 잠시만 기다려 주세요');
                    break;
                case 'FAILURE':
                    dialogHandler.simpleYesModal('경고', '참여 신청을 실패하였습니다. 잠시후 다시 시도해 주세요');
                    break;
                default:
                    alert('default?');
                    break;
            }
        };
        xhr.open('POST', '/message/');
        xhr.send(formData);
    });
}

if ($payButton !== null) {
    $payButton.addEventListener('click', () => {
        // todo 내 지갑 kkiri-pay에서 지불
    });
}

function placesSearchCB(data, status) {
    if (status === kakao.maps.services.Status.OK) {
        const lat = $menuBox.dataset['lat'];
        const lng = $menuBox.dataset['lng'];
        const bounds = new kakao.maps.LatLngBounds();
        for (let i = 0; i < data.length; i++) {
            if (data[i]['x'] === lng && data[i]['y'] === lat) {
                displayMarker(data[i]);
                bounds.extend(new kakao.maps.LatLng(data[i].y, data[i].x));
            }
        }
        map.setBounds(bounds);
    }
}

function displayMarker(place) {
    const marker = new kakao.maps.Marker({
        map: map,
        position: new kakao.maps.LatLng(place.y, place.x),
        clickable: false,
        image: markerImage,
    });
}

ps.keywordSearch($restaurant.innerText, placesSearchCB);