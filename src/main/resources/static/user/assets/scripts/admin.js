const BossConfirm = {
    processApproval: async function (email, status) {
        const actionText = status === 'APPROVED' ? '승인' : '거절';

        const isConfirmed = await new Promise((resolve) => {
            dialogHandler.simpleYesNoModal(`${actionText} 확인`, `${email} 사장님을 ${actionText}하시겠습니까?`, [
                {
                    caption: '취소',
                    onclick: () => resolve(false)
                },
                {
                    caption: '확인',
                    onclick: () => resolve(true)
                }
            ]);
        });

        if (!isConfirmed) return;

        try {
            const response = await fetch('/admin/approve', {
                method: 'PATCH',
                headers: {'Content-Type' : 'application/json'},
                body: JSON.stringify({email: email, status: status})
            });

            const data = await response.json();

            if (data.result === 'success') {
                dialogHandler.simpleYesModal('안내', `${actionText} 처리가 완료되었습니다.`);
                location.reload();
            } else {
                dialogHandler.simpleYesModal('오류', `오류 발생 : ${data.message}`);
            }
        } catch (error) {
            console.error('통신 에러:', error);
            dialogHandler.simpleYesModal('경고', '서버 연결에 실패했습니다.');
        }
    }
}

document.addEventListener('DOMContentLoaded', () => {
    const actionButtons = document.querySelectorAll('.btn-action');

    actionButtons.forEach(button => {
        button.addEventListener('click', (e) => {
            const email = e.currentTarget.getAttribute('data-email');
            const status = e.currentTarget.getAttribute('data-status');

            BossConfirm.processApproval(email, status);
        }) ;
    });
});

const ctx = document.getElementById('visitorChart').getContext('2d');
new Chart(ctx, {
    type: 'line',
    data: {
        labels: ['월', '화', '수', '목', '금', '토', '일'],
        datasets: [{
            label: '방문자 수',
            data: [120, 190, 300, 250, 280, 450, 380],
            borderColor: '#4e73df',
            backgroundColor: '#4e73df22',
            fill: true,
            tension: 0.3
        }]
    },
    options: {
        responsive: true,
        maintainAspectRatio: false
    }
});

document.addEventListener('DOMContentLoaded', () => {
    const rejectButtons = document.querySelectorAll('.btn-reject');

    rejectButtons.forEach(button => {
        button.addEventListener('click', () => {
            const userEmail = button.dataset.email;
            const currentStatus = button.dataset.status;
            const isBlack = (currentStatus === 'BLACK');
            const actionText = isBlack ? '해제' : '차단';

            dialogHandler.simpleYesNoModal('회원 상태 변경', `해당 회원을 [${actionText}] 처리하시겠습니까?`, [
                {
                    caption: '취소'
                },
                {
                    caption: '확인',
                    onclick: () => updateUserStatus(userEmail, isBlack ? 'ACTIVE' : 'BLACK', actionText)
                }
            ])
        })
    })
});

function updateUserStatus(email, newStatus, actionText) {
    fetch('admin/user/update-status', {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json',
        },
        body: JSON.stringify({
            email: email,
            status: newStatus
        })
    }).then(response => response.json())
        .then(data => {
            if (data.success) {
                dialogHandler.simpleYesModal('완료', `${actionText} 처리가 완료되었습니다.`);
                location.reload();
            } else {
                dialogHandler.simpleYesModal('경고', '처리에 실패했습니다.');
            }
        }).catch(error => {
            console.error('Error:', error);
            dialogHandler.simpleYesModal('경고', '서버 통신 오류가 발생했습니다.');
    });
}
