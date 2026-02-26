/** @type {HTMLElement} */
const $searchModal = document.getElementById('searchModal');
/** @type {HTMLElement} */
const $geoMap = document.getElementById('geoMap');
/** @type {HTMLFormElement} */
const $writeForm = document.forms['writeForm'];
/** @type {HTMLFormElement} */
const $searchForm = document.forms['searchForm'];
/** @type {HTMLElement} */
const $searchFormAddr = $searchModal.querySelector(':scope > .modal > .button-container > .caption > .address')
/** @type {HTMLElement} */
const $searchList = $searchForm.querySelector(':scope > .list');
const $addressContainer = $writeForm.querySelector(':scope > .address-container');
/** @type {HTMLInputElement} */
const $radioPrimary = $writeForm.querySelector(':scope > .address-container > .row > .radio.primary');
/** @type {HTMLInputElement} */
const $radioSecondary = $writeForm.querySelector(':scope > .address-container > .row > .radio.secondary');
const ps = new kakao.maps.services.Places();
const markerImage = new kakao.maps.MarkerImage(
    '/article/assets/images/write/search-modal/marker.png',
    new kakao.maps.Size(64, 64),
    {
        offset: new kakao.maps.Point(10, 56)
    }
);
let currentLng = '128.5938';
let currentLat = '35.8661';
let dialogEl;
let modalEl;
let buttonRef;

$searchModal.addEventListener('mousedown', (e) => {
    if (!$searchForm.contains(e.target)) {
        $searchModal.hide();
        if ($geoMap.hasAttribute('data-visible')) {
            $geoMap.querySelector('[name="close"]').click();
        }
    }
});

$searchForm['current'].addEventListener('click', () => {
    geoHandler.getGeoLocation()
        .then(() => {
            switch (geoHandler.error) {
                case 0:
                    geoHandler.lat !== '' && geoHandler.lng !== ''
                        ? dialogHandler.simpleYesModal('알림', '현재 위치를 저장했습니다.', {onclick: () => $searchFormAddr.textContent = geoHandler.addressName})
                        : dialogHandler.simpleYesModal('경고', '위치정보가 제공되지 않았습니다. 잠시후 다시 시도해 보세요');
                    currentLat = geoHandler.lat;
                    currentLng = geoHandler.lng;
                    break;
                case 1:
                    dialogHandler.simpleYesModal('경고', '위치정보 제공을 거부하였습니다. 정확한 검색을 위해 위치정보 제공을 허용해 주세요.');
                    break;
                case 2:
                    dialogHandler.simpleYesModal('경고', '정확한 위치를 알 수 없습니다. 잠시후 다시 시도해 주세요.');
                    break;
                case 3:
                    dialogHandler.simpleYesModal('경고', '요청시간이 초과되었습니다. 잠시후 다시 시도해 주세요.');
                    break;
                default:
            }
        })
        .catch(err => console.error("Error: " + err));
});

$searchForm['select'].addEventListener('click', () => {
    const options = {
        center: new kakao.maps.LatLng(geoHandler.lat === '' ? currentLat : geoHandler.lat, geoHandler.lng === '' ? currentLng : geoHandler.lng),
        level: 3,
    }
    const map = new kakao.maps.Map($geoMap, options);
    const marker = new kakao.maps.Marker({
        position: map.getCenter(),
        clickable: true,
        image: markerImage,
    });
    $geoMap.setAttribute('data-visible', '');
    marker.setMap(map);
    let coords
    kakao.maps.event.addListener(map, 'click', function (mouseEvent) {
        coords = mouseEvent.latLng;
        marker.setPosition(coords);
    });
    kakao.maps.event.addListener(marker, 'click', function () {
        dialogHandler.simpleYesNoModal('알림', '마커를 중심으로 검색을 진행 하시겠습니까?', [{
            caption: "취소",
            onclick: () => {
            }
        }, {
            caption: "확인",
            onclick: () => {
                geoHandler.coordsToAddr(coords.getLng(), coords.getLat())
                    .then((address) => {
                        $searchFormAddr.textContent = address;
                    });
                map.setCenter(new kakao.maps.LatLng(coords.getLat(), coords.getLng()));
                geoHandler.lat = coords.getLat();
                geoHandler.lng = coords.getLng();
                currentLat = coords['Ma'];
                currentLng = coords['La'];
                $geoMap.hide();
            }
        }]);
    });
});

$searchForm['close'].addEventListener('click', () => {
    $geoMap.hide();
});

$searchForm.addEventListener('submit', (e) => {
    e.preventDefault();
    ps.keywordSearch($searchForm['search'].value, placesSearchCB, {
        location: new kakao.maps.LatLng(geoHandler.lat, geoHandler.lng),
        radius: 1000
    });
})

$writeForm['placeButton'].addEventListener('click', () => {
    buttonRef = 'place';
    geoHandler.getGeoLocation()
        .then(() => {
            geoHandler.addressName === ''
                ? $searchFormAddr.textContent = '현 위치를 알 수 없습니다.'
                : $searchFormAddr.textContent = geoHandler.addressName;
        });
    $searchList.querySelector(':scope > .empty').hide();
    $searchModal.show();
});

$writeForm['restaurantButton'].addEventListener('click', () => {
    buttonRef = 'restaurant';
    geoHandler.getGeoLocation()
        .then(() => {
            geoHandler.addressName === ''
                ? $searchFormAddr.textContent = '현 위치를 알 수 없습니다.'
                : $searchFormAddr.textContent = geoHandler.addressName;
        });
    $searchList.querySelector(':scope > .empty').hide();
    $searchModal.show();
});

$addressContainer.addEventListener('change', () => {
    if ($radioPrimary.checked) {
        $writeForm['addressPrimary'].disabled = true;
        $writeForm['findButton'].disabled = false;
        $writeForm['addressSecondary'].disabled = false;
        $writeForm['placeButton'].disabled = true;
    } else if ($radioSecondary.checked) {
        $writeForm['addressPrimary'].disabled = true;
        $writeForm['findButton'].disabled = true;
        $writeForm['addressSecondary'].disabled = true;
        $writeForm['placeButton'].disabled = false;
    }
});

$writeForm.addEventListener('submit', (e) => {
    e.preventDefault();
    console.log('mode:', $writeForm['mode'].value);
    console.log('id:', $writeForm['id'].value);
    const isModify = $writeForm['mode'].value === 'modify';
    // title
    if ($writeForm['title'].value === '') {
        dialogHandler.simpleYesModal('경고', '제목을 입력해 주세요.');
        $writeForm['title'].focus();
        return;
    }
    if (!/^[\s\S]{1,100}$/g.test($writeForm['title'].value)) {
        dialogHandler.simpleYesModal('경고', '1-100자 이내 올바른 제목을 입력해 주세요.');
        $writeForm['title'].focus();
        $writeForm['title'].select();
        return;
    }

    // menu
    if ($writeForm['menu'].value === -1) {
        dialogHandler.simpleYesModal('경고', '메뉴를 선택해 주세요.');
        $writeForm['menu'].focus();
        return;
    }

    // menuName
    if ($writeForm['menuName'].value === '') {
        dialogHandler.simpleYesModal('경고', '메뉴 이름을 입력해 주세요.');
        $writeForm['menuName'].focus();
        return;
    }

    if (!/^[\da-zA-Z가-힣`~!@#$%^&*()\-_=+\[{\]}\\|;:'",<.>/? ]{1,50}$/g.test($writeForm['menuName'].value)) {
        alert("제대로된 메뉴이름");
        dialogHandler.simpleYesModal('경고', '1-50자 이내 올바른 메뉴 이름을 입력해 주세요.');
        $writeForm['menuName'].focus();
        $writeForm['menuName'].select();
        return;
    }

    // minOrderPrice
    if ($writeForm['minOrderPrice'].value === '') {
        dialogHandler.simpleYesModal('경고', '최소주문금액을 입력해 주세요.');
        $writeForm['minOrderPrice'].focus();
        return;
    }
    if (!/^(\d{1,5})$/g.test($writeForm['minOrderPrice'].value)) {
        dialogHandler.simpleYesModal('경고', '10만원 이하 숫자의 최소주문금액을 입력해 주세요.');
        $writeForm['minOrderPrice'].focus();
        $writeForm['minOrderPrice'].select();
        return;
    }

    // orderPrice
    if ($writeForm['orderPrice'].value === '') {
        dialogHandler.simpleYesModal('경고', '주문금액을 입력해 주세요.');
        $writeForm['orderPrice'].focus();
        return;
    }
    if (!/^(\d{1,6})$/g.test($writeForm['orderPrice'].value)) {
        alert("1 ~ 10,000,000원 금액만");
        dialogHandler.simpleYesModal('경고', '100만원 이하 숫자의 주문금액을 입력해 주세요.');
        $writeForm['orderPrice'].focus();
        $writeForm['orderPrice'].select();
        return;
    }

    // deliveryPrice
    if ($writeForm['deliveryPrice'].value === '') {
        dialogHandler.simpleYesModal('경고', '배달비를 입력해 주세요.');
        $writeForm['deliveryPrice'].focus();
        return;
    }
    if (!/^(\d{1,5})$/g.test($writeForm['deliveryPrice'].value)) {
        dialogHandler.simpleYesModal('경고', '10만원 이하 숫자의 배달비를 입력해 주세요.');
        $writeForm['deliveryPrice'].focus();
        $writeForm['deliveryPrice'].select();
        return;
    }

    // orderTime
    const now = new Date().getTime();

    if ($writeForm['orderTime'].value === '') {
        dialogHandler.simpleYesModal('경고', '주문시간을 선택해 주세요.');
        $writeForm['orderTime'].focus();
        return;
    }

    if (new Date($writeForm['orderTime'].value).getTime() < now) {
        dialogHandler.simpleYesModal('경고', '올바른 주문시간을 선택해 주세요.');
        $writeForm['orderTime'].focus();
        return;
    }

    // pickupTime
    if ($writeForm['pickupTime'].value === '') {
        dialogHandler.simpleYesModal('경고', '나눔시간을 선택해 주세요.');
        $writeForm['pickupTime'].focus();
        return;
    }

    if (new Date($writeForm['pickupTime'].value).getTime() < new Date($writeForm['orderTime'].value).getTime()
        || new Date($writeForm['pickupTime'].value).getTime() < now) {
        dialogHandler.simpleYesModal('경고', '올바른 주문시간을 선택해 주세요.');
        $writeForm['pickupTime'].focus();
        return;
    }

    // restaurant
    if ($writeForm['restaurant'].value === '') {
        dialogHandler.simpleYesModal('경고', '가게찾기 버튼을 눌러 가게이름을 입력해 주세요.', {onclick: () => $writeForm['restaurantButton'].click()});
        return;
    }

    const restaurant = $writeForm['restaurant'];

    if (!restaurant.dataset['lng'] || !restaurant.dataset['lat']) {
        dialogHandler.simpleYesModal('경고', '가게를 다시 선택해 주세요.');
        return;
    }

    // lat & lng
    if ($radioPrimary.checked) {
        if ($writeForm['addressPrimary'].dataset['lng'].split('.')[0] < 125
            || $writeForm['addressPrimary'].dataset['lng'].split('.')[0] > 132) {
            dialogHandler.simpleYesModal('경고', '올바른 경도가 아닙니다. 다시 검색해 주세요.');
        }
        if ($writeForm['addressPrimary'].dataset['lat'].split('.')[0] < 33
            || $writeForm['addressPrimary'].dataset['lat'].split('.')[0] > 39) {
            dialogHandler.simpleYesModal('경고', '올바른 위도가 아닙니다. 다시 검색해 주세요.');
        }
    } else if ($radioSecondary.checked) {
        if ($writeForm['addressSecondary'].dataset['lng'].split('.')[0] < 125
            || $writeForm['addressSecondary'].dataset['lng'].split('.')[0] > 132) {
            dialogHandler.simpleYesModal('경고', '올바른 경도가 아닙니다. 다시 검색해 주세요.');
        }
        if ($writeForm['addressSecondary'].dataset['lat'].split('.')[0] < 33
            || $writeForm['addressSecondary'].dataset['lat'].split('.')[0] > 39) {
            dialogHandler.simpleYesModal('경고', '올바른 위도가 아닙니다. 다시 검색해 주세요.');
        }
    }


    // addressPrimary
    if ($radioPrimary.checked) {
        if ($writeForm['addressPrimary'].value === '') {
            dialogHandler.simpleYesModal('경고', '주소찾기 버튼을 눌러 주소를 입력해 주세요.', {onclick: () => $writeForm['findButton'].click()});
            return;
        }
    }
    // addressSecondary
    if ($radioSecondary.checked) {
        if ($writeForm['addressSecondary'].value === '') {
            dialogHandler.simpleYesModal('경고', '장소 또는 상세주소를 입력해 주세요.');
            $writeForm['addressSecondary'].focus();
            return;
        }
    }

    // content
    if ($writeForm['content'].value === '') {
        dialogHandler.simpleYesModal('경고', '내용을 입력해 주세요.');
        $writeForm['content'].focus();
        return;
    }
    if (!/^[\s\S]{1,10000}$/g.test($writeForm['content'].value)) {
        dialogHandler.simpleYesModal('경고', '1-10000자 이내 올바른 내용을 입력해 주세요.');
        $writeForm['content'].focus();
        $writeForm['content'].select();
        return;
    }

    const xhr = new XMLHttpRequest();
    const formData = new FormData();
    formData.append('title', $writeForm['title'].value);
    // formData.append('boardId', "share");
    formData.append('menu', $writeForm['menu'].value);
    formData.append('menuName', $writeForm['menuName'].value);
    formData.append('minOrderPrice', $writeForm['minOrderPrice'].value);
    formData.append('orderPrice', $writeForm['orderPrice'].value);
    formData.append('deliveryPrice', $writeForm['deliveryPrice'].value);
    formData.append('orderTime', $writeForm['orderTime'].value);
    formData.append('pickupTime', $writeForm['pickupTime'].value);
    formData.append('restaurant', $writeForm['restaurant'].value);
    if ($radioPrimary.checked) {
        formData.append('pointLat', $writeForm['addressPrimary'].dataset['lat']);
        formData.append('pointLng', $writeForm['addressPrimary'].dataset['lng']);
        formData.append('addressPrimary', $writeForm['addressPrimary'].value);
        formData.append('addressSecondary', $writeForm['addressSecondary'].value);
    } else if($radioSecondary.checked) {
        formData.append('pointLat', $writeForm['addressSecondary'].dataset['lat']);
        formData.append('pointLng', $writeForm['addressSecondary'].dataset['lng']);
        formData.append('addressSecondary', $writeForm['addressSecondary'].value);
    }
    formData.append('content', $writeForm['content'].value);
    formData.append('IsShareChecked', $writeForm['shareCheck'].value);
    formData.append('IsEntryChecked', $writeForm['entryCheck'].checked);
    if (isModify) {
        const idInput = $writeForm.querySelector('input[name="id"]');
        formData.append('id', idInput.value);
    }
    xhr.onreadystatechange = () => {
        if (xhr.readyState !== XMLHttpRequest.DONE) {
            return;
        }
        if (xhr.status < 200 || xhr.status >= 400) {
            alert(`error ${xhr.status}`);
            return;
        }
        const response = JSON.parse(xhr.responseText);
        const idInput = $writeForm.querySelector('input[name="id"]');
        const isModify = idInput && idInput.value !== '';

        if (isModify) {
            // 수정
            switch (response["result"]) {
                case 'SUCCESS':
                    location.href = `/article/share?id=${idInput.value}`;
                    break;
                case 'FAILURE':
                    alert('수정 실패');
                    break;
                case 'FAILURE_SESSION':
                    alert('세션 만료');
                    break;
                default:
                    alert('알 수 없는 오류');
                    break;
            }
        } else {
            // 작성
            switch (response["articleResult"]) {
                case 'SUCCESS':
                    $writeForm['title'].value = '';
                    $writeForm['menu'].value = '';
                    $writeForm['menuName'].value = '';
                    $writeForm['minOrderPrice'].value = '';
                    $writeForm['orderPrice'].value = '';
                    $writeForm['deliveryPrice'].value = '';
                    $writeForm['orderTime'].value = '';
                    $writeForm['pickupTime'].value = '';
                    $writeForm['restaurant'].value = '';
                    $writeForm['addressCheck'][0].checked = true;
                    $writeForm['addressPrimary'].value = '';
                    $writeForm['addressSecondary'].value = '';
                    $writeForm['content'].value = '';
                    $writeForm['entryCheck'].checked = false;

                    location.href = `/article/share?id=${response.id}`;
                    break;
                case 'FAILURE':
                    alert('failure');
                    break;
                case 'FAILURE_SESSION':
                    alert('failure_session');
                    break;
                default:
                    alert('default ???');
                    break;
            }
        }
    };
    const pathParts = window.location.pathname.split('/');
    const boardType = pathParts[2];

    const actionUrl = isModify
        ? `/article/${boardType}/modify`
        : `/article/${boardType}/write`;
    xhr.open('POST', actionUrl);
    xhr.send(formData);
})

function placesSearchCB(data, status) {
    $searchList.querySelector(':scope > .empty').hide();
    if (status === kakao.maps.services.Status.ZERO_RESULT) {
        $searchList.querySelector(':scope > .empty').show();
    }
    if (status === kakao.maps.services.Status.OK) {
        $searchList.querySelectorAll(':scope > .item').forEach(($li) => $li.remove());
        for (let i = 0; i < data.length; i++) {
            const $li = document.createElement('li');
            $li.classList.add('item');
            const $title = document.createElement('span');
            $title.classList.add('title');
            $title.innerText = data[i]['place_name'];
            const $roadAddrName = document.createElement('span');
            $roadAddrName.classList.add('caption');
            $roadAddrName.innerText = data[i]['road_address_name'];
            const $addrName = document.createElement('span');
            $addrName.classList.add('caption');
            $addrName.innerText = data[i]['address_name'];
            $li.append($title, $roadAddrName, $addrName);
            $li.addEventListener('click', () => {
                if (buttonRef === 'place') {
                    $writeForm['addressSecondary'].value = data[i]['place_name'];
                    $writeForm['addressSecondary'].dataset['lat'] = data[i]['y'];
                    $writeForm['addressSecondary'].dataset['lng'] = data[i]['x'];
                }
                if (buttonRef === 'restaurant') {
                    $writeForm['restaurant'].value = data[i]['place_name'];
                    $writeForm['restaurant'].dataset['lat'] = data[i]['y'];
                    $writeForm['restaurant'].dataset['lng'] = data[i]['x'];
                }
                $searchModal.hide();
                $searchForm['search'].value = '';
                buttonRef = '';
                $searchList.querySelectorAll(':scope > .item').forEach(($li) => $li.remove());
            });
            $searchList.append($li);
        }
    }
}

function openAddressDialog(addressPrimary) {
    if (!dialogEl) {
        dialogEl = document.createElement('div');
        dialogEl.setAttribute('data-address-dialog', '');

        modalEl = document.createElement('div');
        modalEl.setAttribute('data-address-modal', '');

        dialogEl.appendChild(modalEl);
        document.body.appendChild(dialogEl);
    }

    dialogEl.setAttribute('data-visible', '');

    new daum.Postcode({
        oncomplete: function (data) {
            geoHandler.addrToCoords(data.address)
                .then(coords => {
                    addressPrimary.dataset['lat'] = coords[0].y;
                    addressPrimary.dataset['lng'] = coords[0].x;
                })
            addressPrimary.value = data.address;
            closeAddressDialog();
        },
        width: '100%',
        height: '100%'
    }).embed(modalEl);
}

function closeAddressDialog() {
    dialogEl.removeAttribute('data-visible');
    modalEl.innerHTML = '';
}

document.addEventListener('DOMContentLoaded', () => {
    const findButton = $writeForm["findButton"];
    const addressPrimary = $writeForm['addressPrimary'];
    const addressSecondary = $writeForm['addressSecondary'];

    findButton.addEventListener('click', () => {
        openAddressDialog(addressPrimary);
    });

    // 배경 클릭 시 닫기
    document.addEventListener('click', (e) => {
        if (e.target === dialogEl) {
            closeAddressDialog();
            addressSecondary.focus();
        }
    });
});

