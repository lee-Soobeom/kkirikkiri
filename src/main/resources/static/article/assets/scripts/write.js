/** @type {HTMLFormElement} */
const $writeForm = document.forms['writeForm'];

const boardId = $writeForm['boardId'].value;
const title = $writeForm['title'].value;
const menu = $writeForm['menu'].value;
const minOrderPrice = $writeForm['minOrderPrice'].value;
const orderPrice = $writeForm['orderPrice'].value;
const deliveryPrice = $writeForm['deliveryPrice'].value;
const orderTime = $writeForm['orderTime'].value;
const restaurant = $writeForm['restaurant'].value;
const pickupTime = $writeForm['pickupTime'].value;
const addressPostal = $writeForm['addressPostal'].value;
const addressPrimary = $writeForm['addressPrimary'].value;
const addressSecondary = $writeForm['addressSecondary'].value;
const content = $writeForm['content'].value;


let dialogEl;
let modalEl;

$writeForm.addEventListener('submit', (e) => {
    e.preventDefault();
    // boardId
    if ($writeForm['boardId'].value === -1) {
        alert("게시판 선택");
        return;
    }

    // title
    if ($writeForm['title'].value === '') {
        alert("제목 써라");
        $writeForm['title'].focus();
        return;
    }
    if (!/^.{1,100}$/g.test($writeForm['title'].value)) {
        alert("1 ~ 100자");
        $writeForm['title'].focus();
        $writeForm['title'].select();
        return;
    }

    // menu
    if ($writeForm['menu'].value === -1) {
        alert("menu 선택");
        return;
    }

    // minOrderPrice
    if ($writeForm['minOrderPrice'].value === '') {
        alert("최소주문금액 써라");
        $writeForm['minOrderPrice'].focus();
        return;
    }
    if (!/^(\d{1,7})$/g.test($writeForm['minOrderPrice'].value)) {
        alert("1 ~ 99,999,999원 금액만");
        $writeForm['minOrderPrice'].focus();
        $writeForm['minOrderPrice'].select();
        return;
    }

    // orderPrice
    if ($writeForm['orderPrice'].value === '') {
        alert("주문금액 써라");
        $writeForm['orderPrice'].focus();
        return;
    }
    if (!/^(\d{1,7})$/g.test($writeForm['orderPrice'].value)) {
        alert("1 ~ 10,000,000원 금액만");
        $writeForm['orderPrice'].focus();
        $writeForm['orderPrice'].select();
        return;
    }

    // deliveryPrice
    if ($writeForm['deliveryPrice'].value === '') {
        alert("배달비 써라");
        $writeForm['deliveryPrice'].focus();
        return;
    }
    if (!/^(\d{1,7})$/g.test($writeForm['deliveryPrice'].value)) {
        alert("1 ~ 99,999,999원 금액만");
        $writeForm['deliveryPrice'].focus();
        $writeForm['deliveryPrice'].select();
        return;
    }

    // orderTime
    if ($writeForm['orderTime'].value === '') {
        alert("주문시간 써라");
        $writeForm['orderTime'].focus();
        return;
    }
    if (!/^\d{2}:\d{2}$/g.test($writeForm['orderTime'].value)) {
        alert("시간");
        $writeForm['orderTime'].focus();
        $writeForm['orderTime'].select();
        return;
    }

    // restaurant
    if ($writeForm['restaurant'].value === '') {
        alert("가게이름 써라");
        $writeForm['restaurant'].focus();
        return;
    }
    if (!/^.{1,100}$/g.test($writeForm['restaurant'].value)) {
        alert("1 ~ 100자");
        $writeForm['restaurant'].focus();
        $writeForm['restaurant'].select();
        return;
    }

    // pickupTime
    if ($writeForm['pickupTime'].value === '') {
        alert("나눔시간 써라");
        $writeForm['pickupTime'].focus();
        return;
    }
    if (!/^\d{2}:\d{2}$/g.test($writeForm['pickupTime'].value)) {
        alert("1 ~ 99,999,999원 금액만");
        $writeForm['pickupTime'].focus();
        $writeForm['pickupTime'].select();
        return;
    }

    // addressSecondary
    if ($writeForm['addressSecondary'].value === '') {
        alert("제목 써라");
        $writeForm['addressSecondary'].focus();
        return;
    }
    if (!/^.{1,100}$/g.test($writeForm['addressSecondary'].value)) {
        alert("1 ~ 100자");
        $writeForm['addressSecondary'].focus();
        $writeForm['addressSecondary'].select();
        return;
    }

    // content
    if ($writeForm['content'].value === '') {
        alert("제목 써라");
        $writeForm['content'].focus();
        return;
    }
    if (!/^.{1,10000}$/g.test($writeForm['content'].value)) {
        alert("1 ~ 100자");
        $writeForm['content'].focus();
        $writeForm['content'].select();
        return;
    }

    const xhr = new XMLHttpRequest();
    const formData = new FormData();
    formData.append('boardId', $writeForm['boardId'].value);
    formData.append('title', $writeForm['title'].value);
    formData.append('menu', $writeForm['menu'].value);
    formData.append('minOrderPrice', $writeForm['minOrderPrice'].value);
    formData.append('orderPrice', $writeForm['orderPrice'].value);
    formData.append('deliveryPrice', $writeForm['deliveryPrice'].value);
    formData.append('orderTime', $writeForm['orderTime'].value);
    formData.append('restaurant', $writeForm['restaurant'].value);
    formData.append('pickupTime', $writeForm['pickupTime'].value);
    formData.append('addressSecondary', $writeForm['addressSecondary'].value);
    formData.append('content', $writeForm['content'].value);

    xhr.onreadystatechange = () => {
        if (xhr.readyState !== XMLHttpRequest.DONE) {
            return;
        }
        if (xhr.status < 200 || xhr.status >= 400) {
            alert(`error ${xhr.status}`);
            return;
        }
        const response = JSON.parse(xhr.responseText);
        console.log(response);
    };
    xhr.open('POST', '/article/write');
    xhr.send(formData);
})

function openAddressDialog(addressPostal, primaryInput) {
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
            addressPostal.value = data.zonecode;
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
    const findButton = $writeForm["findButton"];
    const addressPostal = $writeForm['addressPostal'];
    const addressPrimary = $writeForm['addressPrimary'];
    const addressSecondary = $writeForm['addressSecondary'];

    findButton.addEventListener('click', () => {
        openAddressDialog(addressPostal, addressPrimary);
    });

    // 배경 클릭 시 닫기
    document.addEventListener('click', (e) => {
        if (e.target === dialogEl) {
            closeAddressDialog();
            addressSecondary.focus();
        }
    });
});

