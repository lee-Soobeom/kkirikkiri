/** @type {HTMLElement} */
const $main = document.getElementById('main');
const $more = document.getElementById('more');
const $my = document.getElementById('my');
const $address = $my.querySelector(':scope > .container > .user > .info > .address');
const $advertisement = $main.querySelector(':scope > .title');
const $orderList = $main.querySelector(':scope > .group');
const $deactivateButton = $more.querySelector('[name="deactivateButton"]');


$deactivateButton.addEventListener('click', () => {
    $more.classList.remove('-show');
});

const intersectionObserver = new IntersectionObserver((entries) => {
    const entry = entries[0];
    if (entry.intersectionRatio === 1) {
        $more.classList.add('-show');
    } else {
        $more.classList.remove('-show');
    }
}, {
    root: null,
    threshold: 1,
    rootMargin: '0% 0% -16px 0%'
});
intersectionObserver.observe($orderList);

setInterval(() => {
    $advertisement.classList.toggle('-active');
}, 2000);

setTimeout(() => $address.innerText = geoHandler.addressName, 100);