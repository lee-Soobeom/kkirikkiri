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

const visitorChartCanvas = document.getElementById('visitorChart');

if (visitorChartCanvas) {
    const ctx = visitorChartCanvas.getContext('2d');
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
}

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

    const gradeButtons = document.querySelectorAll('.btn-grade');

    gradeButtons.forEach(button => {
        button.addEventListener('click', (e) => {
            const tr = e.currentTarget.closest('tr');
            const rejectBtn = tr.querySelector('.btn-reject');
            const userEmail = rejectBtn.dataset.email;
            const currentStatus = rejectBtn.dataset.status;

            const nextStatus = (currentStatus === 'VIP') ? 'GENERAL' : 'VIP';
            const actionText = (nextStatus === 'VIP') ? '우수 회원 승격' : '일반 회원';

            dialogHandler.simpleYesNoModal('등급 변경 확인', `${userEmail} 회원을 [${actionText}] 하시겠습니까?`, [
                {
                    caption: '취소'
                },
                {
                    caption: '확인',
                    onclick: () => updateUserStatus(userEmail, nextStatus, actionText)
                }
            ]);
        });
    });
});

function updateUserStatus(email, newStatus, actionText) {
    fetch('/admin/user/update-status', {
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
            if (data.result === 'success' || data.success) {
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

function openNoticeWrite() {
    document.getElementById('noticeWriteModal').style.display = 'flex';
}

function closeNoticeWrite() {
    document.getElementById('noticeWriteModal').style.display = 'none';
}

function submitNotice() {
    const form = document.forms['noticeForm'];
    const title = form['title'].value.trim();
    const content = form['content'].value.trim();

    if (!title) { dialogHandler.simpleYesModal('안내', '제목을 입력해주세요.'); return; }
    if (!content) { dialogHandler.simpleYesModal('안내', '내용을 입력해주세요.'); return; }

    const formData = new FormData();
    formData.append('boardId', 'notice');
    formData.append('title', title);
    formData.append('content', content);

    const xhr = new XMLHttpRequest();
    xhr.onreadystatechange = () => {
        if (xhr.readyState !== XMLHttpRequest.DONE) return;
        const response = JSON.parse(xhr.responseText);
        if (response.result === 'SUCCESS') {
            dialogHandler.simpleYesModal('안내', '공지가 작성되었습니다.');
            location.reload();
        } else {
            dialogHandler.simpleYesModal('안내', '작성에 실패했습니다.');
        }
    };
    xhr.open('POST', '/article/notice/write');
    xhr.send(formData);
}

function openNoticeEdit(btn) {
    const id = btn.dataset.id;
    const title = btn.dataset.title;
    const content = btn.dataset.content;

    document.getElementById('editNoticeId').value = id;
    document.forms['noticeEditForm']['title'].value = title;
    document.forms['noticeEditForm']['content'].value = content;
    document.getElementById('noticeEditModal').style.display = 'flex';
}

function closeNoticeEdit() {
    document.getElementById('noticeEditModal').style.display = 'none';
}

function submitNoticeEdit() {
    const id = document.getElementById('editNoticeId').value;
    const form = document.forms['noticeEditForm'];
    const title = form['title'].value.trim();
    const content = form['content'].value.trim();

    if (!title) { alert('제목을 입력해주세요.'); return; }
    if (!content) { alert('내용을 입력해주세요.'); return; }

    const formData = new FormData();
    formData.append('id', id);
    formData.append('boardId', 'notice');
    formData.append('title', title);
    formData.append('content', content);

    const xhr = new XMLHttpRequest();
    xhr.onreadystatechange = () => {
        if (xhr.readyState !== XMLHttpRequest.DONE) return;
        const response = JSON.parse(xhr.responseText);
        if (response.result === 'SUCCESS') {
            alert('수정됐습니다.');
            location.reload();
        } else {
            alert('수정에 실패했습니다.');
        }
    };
    xhr.open('POST', '/article/notice/modify');
    xhr.send(formData);
}

function deleteNotice(btn) {
    if (!confirm('정말 삭제하시겠습니까?')) return;
    const id = btn.dataset.id;
    const formData = new FormData();
    formData.append('id', id);

    const xhr = new XMLHttpRequest();
    xhr.onreadystatechange = () => {
        if (xhr.readyState !== XMLHttpRequest.DONE) return;
        dialogHandler.simpleYesModal('안내', '삭제 되었습니다.');
        location.reload();
    };
    xhr.open('POST', '/article/notice/delete');
    xhr.send(formData);
}