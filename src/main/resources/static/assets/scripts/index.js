/** @type {HTMLElement} */
const $main = document.getElementById('main');
const $more = document.getElementById('more');
const $advertisement = $main.querySelector(':scope > .title');
const $orderList = $main.querySelector(':scope > .group');
const $deactivateButton = $more.querySelector('[name="deactivateButton"]');

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
if (navigator.geolocation) {
    navigator.geolocation.getCurrentPosition((position) => {
        console.log("lat " + position.coords.latitude);
        console.log("lon " + position.coords.longitude);
        console.log("time " + new Date(position.timestamp));
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