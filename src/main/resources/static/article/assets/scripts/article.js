const $main = document.getElementById('main');
const $menu = $main.querySelector(':scope > .container > .context-container > .content-container > .order-card > .menu-container > .box.menu > .caption');
const $participateButton = $main?.querySelector('[name="participate"]');
const $payButton = $main?.querySelector('[name="pay"]');
// kakao map
const container = document.getElementById('map');
const point = {
    lat: $main.querySelector('[name="pointLat"]').value,
    lng: $main.querySelector('[name="pointLng"]').value,
}
const options = {
    center: new kakao.maps.LatLng(point.lat, point.lng),
    level: 3
};
const map = new kakao.maps.Map(container, options);
const markerImage = $menu ? new kakao.maps.MarkerImage(
    `/assets/images/main/main.menu.${$menu.innerText}.png`,
    new kakao.maps.Size(120, 120),
    {
        offset: new kakao.maps.Point(60, 57)
    }
) : null;

const marker = new kakao.maps.Marker({
    map: map,
    position: new kakao.maps.LatLng(point.lat, point.lng),
    clickable: false,
    image: markerImage ?? undefined,
});

$participateButton?.addEventListener('click', () => {
    if ($main.querySelector('[name="entryCheck"]').value === 'true') {
        const xhr = new XMLHttpRequest();
        const formData = new FormData();
        formData.append("articleId", new URL(location.href).searchParams.get("id"));
        formData.append("isEntryChecked", "false");
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
                    dialogHandler.simpleYesModal('알림', '공구 게시글에 참여하였습니다.\n실시간 채팅으로 의견을 조율해 보세요.');
                    break;
                case 'FAILURE':
                    dialogHandler.simpleYesModal('경고', '참여 신청을 실패하였습니다. 잠시후 다시 시도해 주세요');
                    break;
                case 'FAILURE_SESSION':
                    dialogHandler.simpleYesModal('경고', '공동구매 참여하기는 로그인 후 이용 가능합니다.');
                    break;
                case 'FAILURE_TIMEOUT':
                    dialogHandler.simpleYesModal('경고', '공동구매 참여하기는 주문 20분 전 마감됩니다. 다른 공동구매에 참여해 주세요.');
                    break;
                default:
                    console.log('default?');
                    break;
            }
        };
        xhr.open('POST', '/participant/add');
        xhr.send(formData);
    } else {
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
                case 'FAILURE_SESSION':
                    dialogHandler.simpleYesModal('경고', '공동구매 참여하기는 로그인 후 이용 가능합니다.');
                    break;
                case 'FAILURE_TIMEOUT':
                    dialogHandler.simpleYesModal('경고', '공동구매 참여하기는 주문 20분 전 마감됩니다. 다른 공동구매에 참여해 주세요.');
                    break;
                default:
                    alert('default?');
                    break;
            }
        };
        xhr.open('POST', '/message/');
        xhr.send(formData);
    }
});

const $slider = document.querySelector('.image-list-container');
if ($slider) {
    const $wrapper = $slider.querySelector('.slide-wrapper');
    const slides = $slider.querySelectorAll('.slide');
    let current = 0;

    // 슬라이드 개수 만큼 wrapper 너비 설정
    $wrapper.style.width = `${slides.length * 100}%`;

    // 각 슬라이드 너비를 전체의 1/n로 설정
    slides.forEach(slide => {
        slide.style.minWidth = `${100 / slides.length}%`;
    });

    $slider.querySelector('.prev')?.addEventListener('click', () => {
        current = (current - 1 + slides.length) % slides.length;
        $wrapper.style.transform = `translateX(-${current * (100 / slides.length)}%)`;
    });

    $slider.querySelector('.next')?.addEventListener('click', () => {
        current = (current + 1) % slides.length;
        $wrapper.style.transform = `translateX(-${current * (100 / slides.length)}%)`;
    });
}

const $imageModal = document.getElementById('imageModal');
if ($imageModal) {
    const $modalImage = $imageModal.querySelector('.image');

    // 슬라이드 이미지 클릭 시 모달 열기
    document.querySelectorAll('.slide > img').forEach(img => {
        img.style.cursor = 'pointer';
        img.addEventListener('click', () => {
            $modalImage.src = img.src;
            $imageModal.classList.add('visible');
        });
    });

    // 배경 or 닫기 버튼 클릭 시 모달 닫기
    $imageModal.querySelector('.backdrop').addEventListener('click', () => {
        $imageModal.classList.remove('visible');
    });
    $imageModal.querySelector('.close').addEventListener('click', () => {
        $imageModal.classList.remove('visible');
    });
}

$payButton?.addEventListener('click', () => {
    dialogHandler.simpleYesNoModal('결제', '공동구매 1인당 주문금액 잔액을 결제하시겠습니까?', [{
        caption: '취소',
        onclick: () => {}
    }, {
        caption: '확인',
        onclick: () => {
            const xhr = new XMLHttpRequest();
            const formData = new FormData();
            formData.append('articleId', new URL(location.href).searchParams.get('id'));
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
                        setTimeout(() => {
                            dialogHandler.simpleYesModal('알림', '결제에 성공하였습니다.');
                        }, 500);
                        break;
                    case 'FAILURE':
                        setTimeout(() => {
                            dialogHandler.simpleYesModal('경고', '결제에 실패하였습니다. 잠시후 다시 시도해 주세요.');
                        }, 500);
                        break;
                    case 'FAILURE_NO_BALANCE':
                        setTimeout(() => {
                            dialogHandler.simpleYesModal('경고','잔액이 부족합니다.\n금액을 충전한 후 다시 결제해주세요.');
                        });
                        break;
                    default:
                }

            };
            xhr.open('PUT', '/wallet/pay');
            xhr.send(formData);
        }
    }]);
});

const ps = new kakao.maps.services.Places();

function placesSearchCB(data, status) {
    if (status === kakao.maps.services.Status.ZERO_RESULT) {

    }
    if (status === kakao.maps.services.Status.OK) {

        for (let i = 0; i < data.length; i++) {
            // 검색 결과
            console.log(data[i]);
        }
    }
}

ps.keywordSearch('검색할 가게 이름', placesSearchCB, { // 가게 이름
    location: new kakao.maps.LatLng(0, 0), // 좌표
    radius: 1000
});