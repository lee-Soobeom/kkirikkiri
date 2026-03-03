document.addEventListener('DOMContentLoaded', () => {
    const $registerForm = document.querySelector('[data-name="socialRegisterForm"]');
    if (!$registerForm) return;

    const $cancelButton = $registerForm.querySelector('[name="cancel"]');
    $cancelButton?.addEventListener('click', () => {
        dialogHandler.simpleYesNoModal(
            '안내',
            '취소 시 자동으로 로그아웃되며, 작성 중인 내용은 저장되지 않습니다. 그래도 진행하시겠습니까?',
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

    const $yearSelect = $registerForm.querySelector('[name="birthYear"]');
    if ($yearSelect) {
        const currentYear = new Date().getFullYear();
        for (let i = currentYear; i >= 1950; i--) {
            const $option = document.createElement('option');
            $option.value = i;
            $option.text = i + '년';
            $yearSelect.appendChild($option);
        }
    }

    $registerForm.querySelector('[name="addressButton"]')?.addEventListener('click', () => {
        new daum.Postcode({
            oncomplete: function(data) {
                $registerForm.querySelector('[name="addressPrimary"]').value = data.address;
                $registerForm.querySelector('[name="addressSecondary"]').focus();
            }
        }).open();
    });

    $registerForm.addEventListener('submit', (e) => {
        e.preventDefault();
        const formData = new FormData($registerForm);
        const y = $registerForm.querySelector('[name="birthYear"]')?.value;
        const m = $registerForm.querySelector('[name="birthMonth"]')?.value;
        const d = $registerForm.querySelector('[name="birthDay"]')?.value;

        if (y && m && d) {
            formData.set('birth', `${y}-${m}-${d}`);
        }

        fetch('/user/social-register', {
            method: 'POST',
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