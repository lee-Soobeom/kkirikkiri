document.addEventListener('DOMContentLoaded', () => {
    const $modifyForm = document.querySelector('[data-name="modifyForm"]');
    if (!$modifyForm) return;

    const $cancelButton = $modifyForm.querySelector('[name="cancel"]');
    $cancelButton?.addEventListener('click', () => {
        dialogHandler.simpleYesNoModal('안내', '수정을 취소하시겠습니까? 현재 작성 중인 내용은 저장되지 않습니다.',
            [
                {
                    caption: "취소"
                },
                {
                    caption: "확인",
                    onclick: () => { location.href = '/'; }
                }
            ]
        );
    });

    $modifyForm.addEventListener('submit', (e) => {
        e.preventDefault();

        const formData = new FormData($modifyForm);

        fetch('/user/my', {
            method: 'PATCH',
            body: formData
        }).then(response => {
            if (!response.ok) throw new Error('서버 통신 에러');
            return response.json();
        }).then(data => {
            if (data.result === 'SUCCESS') {
                dialogHandler.simpleYesModal('알림', '회원 정보가 성공적으로 수정되었습니다.');
                location.href = '/';
            } else {
                dialogHandler.simpleYesModal('경고', '정보 수정에 실패했습니다. 입력값을 확인해 주세요.');
            }
        }).catch(error => {
           console.error('Error : ', error);
           dialogHandler.simpleYesModal('경고', '오류가 발생했습니다. 잠시 후 다시 시도해 주세요.');
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
}

document.getElementById('deleteButton').addEventListener('click', deleteAccount);