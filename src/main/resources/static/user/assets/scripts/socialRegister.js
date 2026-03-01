document.addEventListener('DOMContentLoaded', () => {
    const $registerForm = document.querySelector('[data-name="socialRegisterForm"]');
    if (!$registerForm) return;

    const $cancelButton = $registerForm.querySelector('[name="cancel"]');
    $cancelButton?.addEventListener('click', () => {
        dialogHandler.simpleYesNoModal(
            '안내 🐕‍🦺',
            '취소 시 자동으로 로그아웃되며,<br>작성 중인 내용은 저장되지 않습니다.<br>그래도 진행하시겠습니까?',
            [
                {
                    caption: "계속 작성하기",
                    onclick: () => {

                    }
                },
                {
                    caption: "로그아웃",
                    onclick: () => {
                        location.href = '/user/logout';
                    }
                }
            ]
        );
    });

    $registerForm.querySelector('[name="addressButton"]')?.addEventListener('click', () => {
        new daum.Postcode({
            oncomplete: function(data) {
                $registerForm.querySelector('[name="address"]').value = data.address;
                $registerForm.querySelector('[name="addressDetail"]').focus();
            }
        }).open();
    });

    $registerForm.addEventListener('submit', (e) => {
        e.preventDefault();
        const formData = new FormData($registerForm);

        fetch('/user/social-register', {
            method: 'PATCH',
            body: formData
        }).then(response => response.json())
            .then(data => {
                if (data.result === 'SUCCESS') {
                    dialogHandler.simpleYesModal('환영합니다!', '정보 등록이 완료되었습니다.', {
                        onclick: () => location.href = '/'
                    });
                } else {
                    dialogHandler.simpleYesModal('알림', '입력값을 확인해 주세요.');
                }
            }).catch(err => {
            console.error(err);
            dialogHandler.simpleYesModal('경고', '오류가 발생했습니다.');
        });
    });
});