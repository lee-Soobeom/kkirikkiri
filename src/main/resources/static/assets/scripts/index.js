/** @type {HTMLElement} */
const $main = document.getElementById('main');
const $more = document.getElementById('more');
const $my = document.getElementById('my');
const $address = $my.querySelector(':scope > .container > .user > .info > .address');
const $advertisement = $main.querySelector(':scope > .title');
const $orderList = $main.querySelector(':scope > .group');
const $deactivateButton = $more.querySelector('[name="deactivateButton"]');
const geoHandler = {
    geocoder: new kakao.maps.services.Geocoder(),
    addressName: '',
    addrByPosition: (result, status) => {
        if (status === kakao.maps.services.Status.OK) {
            for(let i = 0; i < result.length; i++) {
                if (result[i].region_type === 'H') {
                    geoHandler.addressName = result[i].address_name;
                    break;
                }
            }
        }
    },
    coordsToAddr: (lng, lat) => {geoHandler.geocoder.coord2RegionCode(lng, lat, geoHandler.addrByPosition)},
    timestamp: '',
};

$deactivateButton.addEventListener('click', () => {
    $more.classList.remove('-show');
});

const intersectionObserver = new IntersectionObserver((entries) => {
    const entry = entries[0];
    if (entry.intersectionRatio === 1) {
        $more.classList.add('-show');
    } else {
        $more.classList.remove('-show');
    }
}, {
    root: null,
    threshold: 1,
    rootMargin: '0% 0% -16px 0%'
});
intersectionObserver.observe($orderList);

// todo: IP 기반 geolocation으로 사용자 위치정보 가져오기 성공 >> 리스트 가져올때 사용자 위치 기반으로 가져오기
// todo: ip2location api 사용해서 ip로 대략적인 위치 정보 받아오기
if (navigator.geolocation) {
    navigator.geolocation.getCurrentPosition((position) => {
        geoHandler.timestamp = new Date(position.timestamp);
        geoHandler.coordsToAddr(position.coords.longitude, position.coords.latitude);
        setTimeout(() => $address.innerText = geoHandler.addressName, 100);
    }, (error) => {
        switch (error.code) {
            case 1:
                console.log("사용자가 위치정보 제공을 거부하였습니다." + `error.code: ${error.code}`);
                break;
            case 2:
                console.log("사용자의 위치를 알 수 없습니다." + `error.code: ${error.code}`);
                break;
            case 3:
                console.log("요청 시간 초과" + `error.code: ${error.code}`);
                break;
            default:
                console.log("알 수 없는 에러");
        }
    }, {
        enableHighAccuracy: false,
        timeout: 5000,
        maximumAge: 0,
    });
} else {
    console.log("no geolocation");
}

setInterval(() => {
    $advertisement.classList.toggle('-active');
}, 2000);