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


function checkSocialLoginStatus() {
    if (typeof needsInfo !== 'undefined' && needsInfo === true) {
        dialogHandler.simpleYesNoModal(
            '추가 정보 입력 필요',
            '끼리끼리 공구 서비스를 이용하시려면 추가적인 정보 등록이 필요합니다.',
            [
                {
                    caption: "다음에 하기",
                    onclick: () => {
                        const overlay = document.createElement('div');
                        overlay.style.cssText = `
                            position: fixed; top: 70px; left: 0; width: 100%; height: calc(100% - 70px);
                            background: rgba(0,0,0,0.1); z-index: 20; cursor: not-allowed;
                        `;
                        document.body.appendChild(overlay);

                        const registerBar = document.createElement('div');
                        registerBar.style.cssText = `
                            position: fixed; bottom: 20px; left: 50%; transform: translateX(-50%);
                            width: 90%; max-width: 500px; background: #FF5C00; color: white;
                            padding: 15px 20px; border-radius: 50px; display: flex;
                            justify-content: space-between; align-items: center;
                            box-shadow: 0 10px 25px rgba(0,0,0,0.2); z-index: 600;
                        `;
                        registerBar.innerHTML = `
                            <span style="font-weight: 600; font-size: 14px;"> 주소를 등록하고 공구에 참여해보세요!</span>
                            <button id="goModifyBtn" style="background: white; color: #FF5C00; border: none; padding: 8px 15px; border-radius: 20px; font-weight: bold; cursor: pointer;">등록하기</button>
                        `;
                        document.body.appendChild(registerBar);

                        document.getElementById('goModifyBtn').onclick = () => {
                            location.href = '/user/social-register';
                        };

                        overlay.onclick = () => {
                            dialogHandler.simpleYesModal('안내', '정보를 등록해야 서비스를 이용할 수 있습니다! 🐕‍🦺');
                        };
                    }
                },
                {
                    caption: "등록하러 가기",
                    onclick: () => {
                        location.href = '/user/social-register';
                    }
                }
            ]
        );
    } else {
        setTimeout(checkSocialLoginStatus, 100);
    }

}

checkSocialLoginStatus();

window.addEventListener('load', () => {
    const urlParams = new URLSearchParams(window.location.search);

    if (typeof isLoggedIn !== 'undefined' && isLoggedIn === false) {
        return;
    }

    const isSocialNeedsInfo = (typeof needsInfo !== 'undefined' && needsInfo === true);
    const isRedirectFromSocial = (urlParams.get('mode') === 'social_reg');

    if (isSocialNeedsInfo || isRedirectFromSocial) {

        dialogHandler.simpleYesNoModal(
            '안내',
            '끼리끼리 공구 서비스를 이용하시려면 추가적인 정보 등록이 필요합니다.',
            [
                {
                    caption: "다음에 하기",
                    onclick: () => {
                        const overlay = document.createElement('div');
                        overlay.style.cssText = `position: fixed; top: 70px; left: 0; width: 100%; height: calc(100% - 70px); background: rgba(0,0,0,0.15); z-index: 20; cursor: not-allowed; backdrop-filter: blur(2px);`;
                        document.body.appendChild(overlay);

                        const registerBar = document.createElement('div');
                        registerBar.style.cssText = `position: fixed; bottom: 30px; left: 50%; transform: translateX(-50%); width: 90%; max-width: 600px; background: #FF5C00; color: white; padding: 18px 25px; border-radius: 50px; display: flex; justify-content: space-between; align-items: center; box-shadow: 0 15px 35px rgba(255, 92, 0, 0.3); z-index: 600;`;
                        registerBar.innerHTML = `<span>지금 정보를 등록하고 공구에 참여해보세요!</span><button id="goReg" style="background: white; color: #FF5C00; border: none; padding: 10px 20px; border-radius: 25px; font-weight: bold; cursor: pointer;">등록하러 가기</button>`;
                        document.body.appendChild(registerBar);

                        document.getElementById('goReg').onclick = () => location.href = '/user/social-register';
                        overlay.onclick = () => dialogHandler.simpleYesModal('안내', '정보를 등록해야 서비스 이용이 가능합니다.');
                    }
                },
                {
                    caption: "등록하러 가기",
                    onclick: () => location.href = '/user/social-register'
                }
            ]
        );

        window.needsInfo = false;
    }
});