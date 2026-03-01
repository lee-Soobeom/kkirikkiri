document.addEventListener('DOMContentLoaded', () => {
    const btnResetPassword = document.getElementById('btnResetPassword');

    btnResetPassword.addEventListener('click', async () => {
        const email = document.getElementById('resetEmail').value;
        const token = document.getElementById('resetToken').value;
        const password = document.getElementById('newPassword').value;
        const confirmPassword = document.getElementById('confirmPassword').value;

        if (password.length < 8) {
            dialogHandler.simpleYesModal('알림', '비밀번호는 8자 이상이어야 합니다.');
            return;
        }

        if (password !== confirmPassword) {
            dialogHandler.simpleYesModal('알림', '비밀번호가 서로 일치하지 않습니다.');
            return;
        }

        try {
            const response = await fetch('/user/reset-password', {
                method: 'PATCH',
                headers: { 'Content-Type': 'application/json' },
                body: JSON.stringify({ email, token, password })
            });

            const data = await response.json();

            if (data.result === 'success') {
                dialogHandler.simpleYesModal('완료', '비밀번호가 성공적으로 변경되었습니다. 다시 로그인해 주세요.');
                location.href = '/user/login';
            } else {
                dialogHandler.simpleYesModal('오류', data.message || '비밀번호 변경에 실패했습니다.');
            }
        } catch (e) {
            dialogHandler.simpleYesModal('오류', '통신 중 문제가 발생했습니다.');
        }
    });
});