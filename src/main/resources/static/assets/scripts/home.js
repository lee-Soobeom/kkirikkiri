/** @type {HTMLElement} */
const $main = document.getElementById('main');
const $address = $my.querySelector(':scope > .container > .user > .info > .address');
const $advertisement = $main.querySelector(':scope > .title');

setInterval(() => {
    $advertisement.classList.toggle('-active');
}, 2000);

setTimeout(() => $address.innerText = geoHandler.addressName, 100);