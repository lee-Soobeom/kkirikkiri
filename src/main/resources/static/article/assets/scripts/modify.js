// /** @type {HTMLFormElement} */
// const $modifyForm = document.forms['modifyForm'];
//
// let dialogEl;
// let modalEl;
//
// /* =========================
//    submit
// ========================= */
// $modifyForm.addEventListener('submit', (e) => {
//     e.preventDefault();
//
//     // boardId
//     if ($modifyForm['boardId'].value === '-1') {
//         alert('게시판 선택');
//         return;
//     }
//
//     // title
//     if ($modifyForm['title'].value === '') {
//         alert('제목 써라');
//         $modifyForm['title'].focus();
//         return;
//     }
//     if (!/^.{1,100}$/.test($modifyForm['title'].value)) {
//         alert('1 ~ 100자');
//         $modifyForm['title'].focus();
//         $modifyForm['title'].select();
//         return;
//     }
//
//     // menu
//     if ($modifyForm['menu'].value === '-1') {
//         alert('menu 선택');
//         return;
//     }
//
//     // minOrderPrice
//     if ($modifyForm['minOrderPrice'].value === '') {
//         alert('최소주문금액 써라');
//         $modifyForm['minOrderPrice'].focus();
//         return;
//     }
//     if (!/^(\d{1,7})$/.test($modifyForm['minOrderPrice'].value)) {
//         alert('1 ~ 99,999,999원 금액만');
//         $modifyForm['minOrderPrice'].focus();
//         $modifyForm['minOrderPrice'].select();
//         return;
//     }
//
//     // orderPrice
//     if ($modifyForm['orderPrice'].value === '') {
//         alert('주문금액 써라');
//         $modifyForm['orderPrice'].focus();
//         return;
//     }
//     if (!/^(\d{1,7})$/.test($modifyForm['orderPrice'].value)) {
//         alert('1 ~ 10,000,000원 금액만');
//         $modifyForm['orderPrice'].focus();
//         $modifyForm['orderPrice'].select();
//         return;
//     }
//
//     // deliveryPrice
//     if ($modifyForm['deliveryPrice'].value === '') {
//         alert('배달비 써라');
//         $modifyForm['deliveryPrice'].focus();
//         return;
//     }
//     if (!/^(\d{1,7})$/.test($modifyForm['deliveryPrice'].value)) {
//         alert('1 ~ 99,999,999원 금액만');
//         $modifyForm['deliveryPrice'].focus();
//         $modifyForm['deliveryPrice'].select();
//         return;
//     }
//
//     // orderTime
//     if ($modifyForm['orderTime'].value === '') {
//         alert('주문시간 써라');
//         $modifyForm['orderTime'].focus();
//         return;
//     }
//     if (!/^\d{2}:\d{2}$/.test($modifyForm['orderTime'].value)) {
//         alert('시간 형식(HH:MM)');
//         $modifyForm['orderTime'].focus();
//         $modifyForm['orderTime'].select();
//         return;
//     }
//
//     // restaurant
//     if ($modifyForm['restaurant'].value === '') {
//         alert('가게이름 써라');
//         $modifyForm['restaurant'].focus();
//         return;
//     }
//     if (!/^.{1,100}$/.test($modifyForm['restaurant'].value)) {
//         alert('1 ~ 100자');
//         $modifyForm['restaurant'].focus();
//         $modifyForm['restaurant'].select();
//         return;
//     }
//
//     // pickupTime
//     if ($modifyForm['pickupTime'].value === '') {
//         alert('나눔시간 써라');
//         $modifyForm['pickupTime'].focus();
//         return;
//     }
//     if (!/^\d{2}:\d{2}$/.test($modifyForm['pickupTime'].value)) {
//         alert('시간 형식(HH:MM)');
//         $modifyForm['pickupTime'].focus();
//         $modifyForm['pickupTime'].select();
//         return;
//     }
//
//     // addressSecondary
//     if ($modifyForm['addressSecondary'].value === '') {
//         alert('상세주소 써라');
//         $modifyForm['addressSecondary'].focus();
//         return;
//     }
//     if (!/^.{1,100}$/.test($modifyForm['addressSecondary'].value)) {
//         alert('1 ~ 100자');
//         $modifyForm['addressSecondary'].focus();
//         $modifyForm['addressSecondary'].select();
//         return;
//     }
//
//     // content
//     if ($modifyForm['content'].value === '') {
//         alert('내용 써라');
//         $modifyForm['content'].focus();
//         return;
//     }
//     if (!/^.{1,10000}$/.test($modifyForm['content'].value)) {
//         alert('1 ~ 10,000자');
//         $modifyForm['content'].focus();
//         $modifyForm['content'].select();
//         return;
//     }
//
//     /* =========================
//        전송
//     ========================= */
//     const xhr = new XMLHttpRequest();
//     const formData = new FormData();
//
//     formData.append('articleId', $modifyForm.dataset.articleId);
//     formData.append('boardId', $modifyForm['boardId'].value);
//     formData.append('title', $modifyForm['title'].value);
//     formData.append('menu', $modifyForm['menu'].value);
//     formData.append('minOrderPrice', $modifyForm['minOrderPrice'].value);
//     formData.append('orderPrice', $modifyForm['orderPrice'].value);
//     formData.append('deliveryPrice', $modifyForm['deliveryPrice'].value);
//     formData.append('orderTime', $modifyForm['orderTime'].value);
//     formData.append('restaurant', $modifyForm['restaurant'].value);
//     formData.append('pickupTime', $modifyForm['pickupTime'].value);
//     formData.append('addressPostal', $modifyForm['addressPostal'].value);
//     formData.append('addressPrimary', $modifyForm['addressPrimary'].value);
//     formData.append('addressSecondary', $modifyForm['addressSecondary'].value);
//     formData.append('content', $modifyForm['content'].value);
//
//     xhr.onreadystatechange = () => {
//         if (xhr.readyState !== XMLHttpRequest.DONE) return;
//
//         if (xhr.status < 200 || xhr.status >= 400) {
//             alert(`error ${xhr.status}`);
//             return;
//         }
//
//         const response = JSON.parse(xhr.responseText);
//         console.log(response);
//
//         // 필요하면 이동
//         // location.href = `/article/read?id=${response.articleId}`;
//     };
//
//     xhr.open('POST', '/article/modify');
//     xhr.send(formData);
// });
//
// /* =========================
//    주소 찾기
// ========================= */
// function openAddressDialog(addressPostal, primaryInput) {
//     if (!dialogEl) {
//         dialogEl = document.createElement('div');
//         dialogEl.setAttribute('data-address-dialog', '');
//
//         modalEl = document.createElement('div');
//         modalEl.setAttribute('data-address-modal', '');
//
//         dialogEl.appendChild(modalEl);
//         document.body.appendChild(dialogEl);
//     }
//
//     dialogEl.setAttribute('data-visible', '');
//
//     new daum.Postcode({
//         oncomplete: (data) => {
//             addressPostal.value = data.zonecode;
//             primaryInput.value = data.address;
//             closeAddressDialog();
//         },
//         width: '100%',
//         height: '100%'
//     }).embed(modalEl);
// }
//
// function closeAddressDialog() {
//     dialogEl.removeAttribute('data-visible');
//     modalEl.innerHTML = '';
// }
//
// /* =========================
//    DOMContentLoaded
// ========================= */
// document.addEventListener('DOMContentLoaded', () => {
//     const findButton = $modifyForm['findButton'];
//     const addressPostal = $modifyForm['addressPostal'];
//     const addressPrimary = $modifyForm['addressPrimary'];
//     const addressSecondary = $modifyForm['addressSecondary'];
//
//     findButton.addEventListener('click', () => {
//         openAddressDialog(addressPostal, addressPrimary);
//     });
//
//     document.addEventListener('click', (e) => {
//         if (e.target === dialogEl) {
//             closeAddressDialog();
//             addressSecondary.focus();
//         }
//     });
// });