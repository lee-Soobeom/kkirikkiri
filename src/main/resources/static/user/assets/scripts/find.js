document.addEventListener('DOMContentLoaded', () => {
    const tabButtons = document.querySelectorAll('.tab-btn');
    const tabContents = document.querySelectorAll('.tab-content');

    tabButtons.forEach(button => {
       button.addEventListener('click', () => {
          const targetTab = button.dataset.tab;

          tabButtons.forEach(btn => btn.classList.remove('active'));
          button.classList.add('active');

          tabContents.forEach(content => {
              content.classList.remove('active');
              if (content.id === targetTab) {
                  content.classList.add('active');
              }
          });
       });
    });

    const findEmailButton = document.getElementById('btnFindEmail');
    if (findEmailButton) {
        findEmailButton.addEventListener('click', async () => {
           const contact = document.getElementById('findEmailContact').value;

           if (!contact) {
               dialogHandler.simpleYesModal('경고', '연락처를 입력해 주세요.');
               return;
           }

           try {
               const response = await fetch(`/user/find-email?contact=${contact}`);
               const data = await response.json();

               const emailResult = document.getElementById('emailResult');
               const foundedEmail = document.getElementById('foundedEmail');

               if (data.result === 'success') {
                   foundedEmail.innerText = data.email;
                   emailResult.style.display = 'block';
               } else {
                   emailResult.style.display = 'none';
                   dialogHandler.simpleYesModal('안내', '일치하는 계정 정보가 없습니다. 다시 입력해 주세요.');
               }
           } catch (error) {
               console.error('통신 에러:', error);
               dialogHandler.simpleYesModal('오류', '서버와 통신 중 문제가 발생했습니다.');
           }
        });
    }

    const btnSendAuthCode = document.getElementById('btnSendAuthCode');
    if (btnSendAuthCode) {
        btnSendAuthCode.addEventListener('click', async () => {
            const email = document.getElementById('resetPasswordEmail').value;

            if (!email) {
                dialogHandler.simpleYesModal('경고', '이메일을 입력해 주세요.');
                return;
            }
            try {
                const response = await fetch('/user/send-auth-code', {
                    method: 'POST',
                    headers: {'Content-Type': 'application/json'},
                    body: JSON.stringify({email})
                });
                const data = await response.json();

                if (data.result === 'success') {
                    dialogHandler.simpleYesModal('알림', '인증번호가 발송되었습니다. 메일함을 확인해 주세요.');
                    document.getElementById('authCodeWrapper').style.display = 'block';
                } else {
                    dialogHandler.simpleYesModal('오류', '가입되지 않은 이메일입니다.')
                }
            } catch (error) {
                dialogHandler.simpleYesModal('오류', '메일 발송 중 문제가 발생했습니다.');
            }
        });
    }

    const verifyCodeButton = document.getElementById('btnVerifyCode');
    if (verifyCodeButton) {
        verifyCodeButton.addEventListener('click', async () => {
            const email = document.getElementById('resetPasswordEmail').value;
            const authCode = document.getElementById('authCode').value;

            if (!authCode) {
                dialogHandler.simpleYesModal('경고', '인증번호를 6자리를 입력해 주세요.');
                return;
            }

            try {
                const response = await fetch('/user/verify-code', {
                    method: 'POST',
                    headers: {'Content-Type': 'application/json'},
                    body: JSON.stringify({email, authCode})
                });
                const data = await response.json();

                if (data.result === 'success') {
                    dialogHandler.simpleYesModal('성공', '인증되었습니다. 비밀번호를 다시 설정하기 위해 페이지를 이동합니다.', {
                        onclick: () => {
                            location.href = `/user/reset-password?email=${email}&token=${data.token}`;
                        }
                    });
                } else {
                    dialogHandler.simpleYesModal('오류', '인증번호가 일치하지 않습니다.');
                }
            } catch (error) {
                dialogHandler.simpleYesModal('오류', '인증 확인 중 문제가 발생했습니다.');
            }
        });
    }
});