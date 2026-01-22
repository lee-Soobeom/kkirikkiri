let dialogEl;
let modalEl;

function openAddressDialog(primaryInput) {
    if (!dialogEl) {
        dialogEl = document.createElement('div');
        dialogEl.setAttribute('data-address-dialog', '');

        modalEl = document.createElement('div');
        modalEl.setAttribute('data-address-modal', '');

        dialogEl.appendChild(modalEl);
        document.body.appendChild(dialogEl);
    }

    dialogEl.setAttribute('data-visible', '');

    new daum.Postcode({
        oncomplete: function (data) {
            primaryInput.value = data.address;
            closeAddressDialog();
        },
        width: '100%',
        height: '100%'
    }).embed(modalEl);
}

function closeAddressDialog() {
    dialogEl.removeAttribute('data-visible');
    modalEl.innerHTML = '';
}

document.addEventListener('DOMContentLoaded', () => {
    const findButton = document.querySelector('[name="findButton"]');
    const primaryInput = document.getElementById('primaryInput');
    const secondaryInput = document.getElementById('secondaryInput');

    findButton.addEventListener('click', () => {
        openAddressDialog(primaryInput);
    });

    // 배경 클릭 시 닫기
    document.addEventListener('click', (e) => {
        if (e.target === dialogEl) {
            closeAddressDialog();
            secondaryInput.focus();
        }
    });
});
