document.addEventListener('DOMContentLoaded', () => {
    const $modifyForm = document.querySelector('[data-name="modifyForm"]');
    if (!$modifyForm) return;

    const $cancelButton = $modifyForm.querySelector('[name="cancel"]');
    $cancelButton?.addEventListener('click', () => {
        dialogHandler.simpleYesNoModal('안내', '수정을 취소하시겠습니까? 현재 작성 중인 내용은 저장되지 않습니다.',
            [
                { caption: "취소" },
                {
                    caption: "확인",
                    onclick: () => { location.href = '/'; }
                }
            ]
        );
    });

    $modifyForm.addEventListener('change', (e) => {
        if (e.target.classList.contains('image-change')) {
            const file = e.target.files[0];
            if (file) {
                if (!file.type.startsWith('image/')) {
                    dialogHandler.simpleYesModal('경고', '이미지 파일만 선택 가능합니다.');
                    e.target.value = '';
                    return;
                }

                const reader = new FileReader();
                const $uploadButton = e.target.closest('.upload-button');

                reader.onload = (event) => {
                    if ($uploadButton) {
                        $uploadButton.style.backgroundImage = `url(${event.target.result})`;
                    }
                };
                reader.readAsDataURL(file);
            }
        }
    });

    $modifyForm.addEventListener('submit', (e) => {
        e.preventDefault();

        const formData = new FormData($modifyForm);

        fetch('/user/my', {
            method: 'POST',
            body: formData
        }).then(response => {
            if (!response.ok) throw new Error('서버 통신 에러');
            return response.json();
        }).then(data => {
            if (data.result === 'SUCCESS') {
                dialogHandler.simpleYesModal('알림', '회원 정보가 성공적으로 수정되었습니다.', {
                    onclick: () => {location.href = '/'}
                });

            } else {
                dialogHandler.simpleYesModal('경고', '정보 수정에 실패했습니다. 입력값을 확인해 주세요.');
            }
        }).catch(error => {
            console.error('Error : ', error);
            dialogHandler.simpleYesModal('경고', '오류가 발생했습니다. 잠시 후 다시 시도해 주세요.');
        });
    });

    const $geoButton = $modifyForm.querySelector('[name="geoAddress"]');
    $geoButton?.addEventListener('click', () => {
        setCurrentLocation($modifyForm);
    });

    const $deleteButton = document.getElementById('deleteButton');
    $deleteButton?.addEventListener('click', deleteAccount);

    // 주소 모달 관련 요소 (HTML에 추가한 ID 기준)
    const $addressLayer = document.getElementById('addressLayer');
    const $addressContent = document.getElementById('addressContent');
    const $btnCloseLayer = document.getElementById('btnCloseLayer');

// 배경 클릭 시 닫기 & 닫기 버튼 로직
    $addressLayer?.addEventListener('click', (e) => {
        if (e.target === $addressLayer) $addressLayer.style.display = 'none';
    });
    $btnCloseLayer?.addEventListener('click', () => {
        $addressLayer.style.display = 'none';
    });

    const addressButtons = document.querySelectorAll('button[name="addressButton"], button[name="storeAddressButton"]');

    addressButtons.forEach(button => {
        button.addEventListener('click', () => {
            const $row = button.closest('.row');
            const addressInput = $row.querySelector('input[name="address"], input[name="addressPrimary"]');

            $addressContent.innerHTML = '';
            $addressContent.appendChild($btnCloseLayer);

            new daum.Postcode({
                oncomplete: function (data) {
                    let addr = data.userSelectedType === 'R' ? data.roadAddress : data.jibunAddress;
                    addressInput.value = addr;

                    $addressLayer.style.display = 'none';

                    const detailInput = addressInput.closest('.change-container')?.querySelector('input[name="addressDetail"]');
                    if (detailInput) detailInput.focus();
                },
                width: '100%',
                height: '100%'
            }).embed($addressContent);

            $addressLayer.style.display = 'block';
        });
    });
});


const processDelete = () => {
    fetch('/user/deleteUser', {
        method: 'DELETE'
    }).then(response => response.json())
        .then(data => {
            if (data.result === 'SUCCESS') {
                dialogHandler.simpleYesModal('안내', '탈퇴가 완료되었습니다.');
                location.href = '/';
            } else {
                dialogHandler.simpleYesModal('경고', '처리 중 오류가 발생했습니다.');
            }
        });
};

const deleteAccount = () => {
    dialogHandler.simpleYesNoModal('안내', '회원 탈퇴를 계속 진행할까요? 탈퇴 시 모든 정보가 삭제됩니다.',
        [
            {
                caption: '취소',
                onclick: () => { location.href = '/'; }
            },
            {
                caption: '확인',
                onclick: processDelete
            }
        ]
    )
};

const geoHandler = new kakao.maps.services.Geocoder();

/** @param {HTMLFormElement} $element */
const setCurrentLocation = ($element) => {
    if (navigator.geolocation) {
        navigator.geolocation.getCurrentPosition((position) => {
            const lat = position.coords.latitude;
            const lng = position.coords.longitude;

            geoHandler.coord2Address(lng, lat, (result, status) => {
                if (status === kakao.maps.services.Status.OK) {
                    $element.querySelector('[name="address"]').value = result[0].address.address_name;
                }
            });
        });
    } else {
        dialogHandler.simpleYesModal('안내', '이 브라우저에서는 위치 서비스를 지원하지 않습니다.');
    }
};