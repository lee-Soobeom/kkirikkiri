/** @type {HTMLElement} */
const $main = document.getElementById('main');
const $advertisement = $main.querySelector(':scope > .title');
const $groupList = $main.querySelector(':scope > .group > .list');
const $loadingMessage = $main.querySelector(':scope > .group > .list > .loading');
const $emptyMessage = $main.querySelector(':scope > .group > .list > .empty');
const homeLocation = JSON.parse(localStorage.getItem("location"));

const xhr = new XMLHttpRequest();
const formData = new FormData();
formData.append("lat", homeLocation.lat);
formData.append("lng", homeLocation.lng);
xhr.onreadystatechange = () => {
    if (xhr.readyState !== XMLHttpRequest.DONE) {
        return;
    }
    $loadingMessage.classList.add('-hidden');
    if (xhr.status < 200 || xhr.status >= 400) {
        dialogHandler.simpleYesModal('오류', `공구 게시글을 불러오는 도중 오류가 발생했습니다. 잠시 후 다시 시도해 주세요. (${xhr.status})`);
        return;
    }
    const response = JSON.parse(xhr.responseText);
    if (response['articles'].length === 0) {
        $emptyMessage.classList.remove('-hidden');
    } else {
        const domParser = new DOMParser();
        response['articles'].forEach((article) => {
            const $li = domParser.parseFromString(`
            <li class="row"
            onclick="location.href='/article/share?id=${article.id}';">
                <div class="image-container">
                    <img class="image" alt=""
                    src="/assets/images/main/main.menu.${article.menu}.png">
                </div>
                <div class="content-container">
                    <h2 class="title">${article.title}</h2>
                    <div class="content">
                        <div class="item">
                            <span class="title">주문:</span>
                            <span class="caption">${article.orderTime.split('T')[1].substring(0,5)}</span>
                        </div>
                        <div class="item">
                            <span class="title">가게:</span>
                            <span class="caption">${article.restaurant}</span>
                        </div>
                    </div>
                    <div class="content">
                        <div class="item">
                            <span class="title">모임:</span>
                            <span class="caption">${article.pickupTime.split('T')[1].substring(0,5)}</span>
                        </div>
                        <div class="item">
                            <span class="title">장소:</span>
                            <span class="caption">${article.addressPrimary + ' ' + article.addressSecondary}</span>
                        </div>
                    </div>
                </div>
                <div class="member-container">
                    <span class="now">1</span>
                    <span>/</span>
                    <span class="max">5</span>
                </div>
            </li>
            `, 'text/html').querySelector('li.row');
            $groupList.append($li);
        });
    }
};
$loadingMessage.classList.remove('-hidden');
xhr.open('POST', '/');
xhr.send(formData);

setInterval(() => {
    $advertisement.classList.toggle('-active');
}, 2000);