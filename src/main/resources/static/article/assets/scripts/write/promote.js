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
const $fileList = $writeForm.querySelectorAll(':scope > .image-upload-container > .list > .item');
const ps = new kakao.maps.services.Places();
const markerImage = new kakao.maps.MarkerImage(
    '/article/assets/images/write/search-modal/marker.png',
    new kakao.maps.Size(40, 40),
    {
        offset: new kakao.maps.Point(3, 40)
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

$writeForm['restaurantButton'].addEventListener('click', () => {
    buttonRef = 'restaurant';
    geoHandler.addressName === ''
        ? $searchFormAddr.textContent = '현 위치를 알 수 없습니다.'
        : $searchFormAddr.textContent = geoHandler.addressName;
    $searchModal.show();
});

$writeForm['files'].addEventListener('change', () => {
    if ($writeForm['files'].files.length !== 0) {
        for (let $li of $fileList) {
            $li.innerText = "이미지를 업로드 해주세요.";
        }
    }
    const MAX_FILE_SIZE = 5 * 1024 * 1024;
    const filesArray = $writeForm['files'].files;
    if (filesArray.length > 10) {
        alert("사진 업로드는 최대 10장까지만 가능합니다.");
        return;
    }
    for (const file of filesArray) {
        if (!file.type.startsWith('image/')) {
            alert("사진만 업로드 가능합니다.");
            return;
        }
        if (file.size > MAX_FILE_SIZE) {
            alert("5MB 이하 크기의 사진만 업로드 가능합니다.");
            return;
        }
    }
    if (filesArray.length === 0) {
        return;
    }
    for (let i = 0; i < filesArray.length; i++) {
        const reader = new FileReader();
        reader.onload = () => {
            $fileList[i].innerHTML = `
                <img class="image" alt="" src="${reader.result}">
            `;
        }
        reader.onerror = () => {
            alert('파일을 가져오는데 실패했습니다.');
        }
        reader.readAsDataURL(filesArray[i]);
    }
    $writeForm.querySelector(':scope > .image-upload-container > .caption > .count').innerText = filesArray.length;
});

$writeForm.addEventListener('submit', (e) => {
    e.preventDefault();
    const isModify = $writeForm['mode'] && $writeForm['mode'].value === 'modify';
    // boardId
    if ($writeForm['boardId'].value === -1) {
        dialogHandler.simpleYesModal('경고', '게시판을 선택해 주세요.');
        $writeForm['boardId'].focus();
        return;
    }

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

    // restaurant
    if ($writeForm['restaurant'].value === '') {
        dialogHandler.simpleYesModal('경고', '가게찾기 버튼을 눌러 가게이름을 입력해 주세요.', {onclick: () => $writeForm['restaurantButton'].click()});
        return;
    }

    // content
    if ($writeForm['content'].value === '') {
        dialogHandler.simpleYesModal('경고', '내용을 입력해 주세요.');
        $writeForm['content'].focus();
        return;
    }
    if (!/^.{1,10000}$/g.test($writeForm['content'].value)) {
        dialogHandler.simpleYesModal('경고', '1-10000자 이내 올바른 내용을 입력해 주세요.');
        $writeForm['content'].focus();
        $writeForm['content'].select();
        return;
    }

    const pathParts = window.location.pathname.split('/');
    const boardType = pathParts[2];
    const xhr = new XMLHttpRequest();
    const formData = new FormData();
    formData.append('boardId', $writeForm['boardId'].value);
    formData.append('title', $writeForm['title'].value);
    formData.append('restaurant', $writeForm['restaurant'].value);
    formData.append('addressPrimary', $writeForm['restaurantAddress'].value);
    formData.append('content', $writeForm['content'].value);
    formData.append('pointLat', $writeForm['pointLat'].value);
    formData.append('pointLng', $writeForm['pointLng'].value);
    if (isModify) {
        const idInput = $writeForm.querySelector('input[name="id"]');
        formData.append('id', idInput.value);
    }
    const files = $writeForm['files'].files;
    for (let i = 0; i < files.length; i++) {
        formData.append('files', files[i]);
    }

    xhr.onreadystatechange = () => {
        if (xhr.readyState !== XMLHttpRequest.DONE) return;
        if (xhr.status < 200 || xhr.status >= 400) {
            alert(`error ${xhr.status}`);
            return;
        }
        const response = JSON.parse(xhr.responseText);
        const idInput = $writeForm.querySelector('input[name="id"]');
        const isModify = idInput && idInput.value !== '';

        if (isModify) {
            switch (response["result"]) {
                case 'SUCCESS':
                    location.href = `/article/promote?id=${idInput.value}`;
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
            switch (response["result"]) {
                case 'SUCCESS':
                    $writeForm['boardId'].value = '';
                    $writeForm['title'].value = '';
                    $writeForm['restaurant'].value = '';
                    $writeForm['content'].value = '';
                    location.href = `/article/promote?id=${response.id}`;
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
    const actionUrl = isModify
        ? `/article/${boardType}/modify`
        : `/article/${boardType}/write`;
    xhr.open('POST', actionUrl);
    xhr.send(formData);
})

function placesSearchCB(data, status) {
    if (status === kakao.maps.services.Status.ZERO_RESULT) {
        $searchList.querySelector(':scope > .empty').show();
    }
    if (status === kakao.maps.services.Status.OK) {
        $searchList.querySelectorAll(':scope > .item').forEach(($li) => $li.remove());
        for (let i = 0; i < data.length; i++) {
            const $li = document.createElement('li');
            $li.classList.add('item');
            $li.dataset['lng'] = data[i]['x'];
            $li.dataset['lat'] = data[i]['y'];
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
                if (buttonRef === 'restaurant') {
                    $writeForm['restaurant'].value = data[i]['place_name'];
                    $writeForm['restaurantAddress'].value = data[i]['road_address_name'] || data[i]['address_name'];
                    $writeForm['pointLat'].value = data[i]['y'];
                    $writeForm['pointLng'].value = data[i]['x'];
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
