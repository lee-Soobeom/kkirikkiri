const $main = document.getElementById('main');
const $restaurant = $main.querySelector(':scope > .container > .context-container > .content-container > .order-container > .box.restaurant > .caption');
const $location = $main.querySelector(':scope > .container > .context-container > .content-container > .order-container > .box.location > .caption');
const container = document.getElementById('map');
const options = {
    center: new kakao.maps.LatLng(33.450701, 126.570667),
    level: 3
};

const map = new kakao.maps.Map(container, options);
const ps = new kakao.maps.services.Places();
ps.keywordSearch($restaurant.innerText, placesSearchCB);

function placesSearchCB(data, status) {
    if (status === kakao.maps.services.Status.OK) {
        const bounds = new kakao.maps.LatLngBounds();

        for (let i = 0; i < data.length; i++) {
            console.log(data[i]);
            displayMarker(data[i]);
            bounds.extend(new kakao.maps.LatLng(data[i].y, data[i].x));
        }

        // 검색된 장소 위치를 기준으로 지도 범위를 재설정합니다
        map.setBounds(bounds);
    }
}

function displayMarker(place) {
    const marker = new kakao.maps.Marker({
        map: map,
        position: new kakao.maps.LatLng(place.y, place.x),
    });
}