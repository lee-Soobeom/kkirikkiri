HTMLElement.VISIBLE = 'data-visible';
/** @return {HTMLElement} */
HTMLElement.prototype.hide = function () {
    this.removeAttribute(HTMLElement.VISIBLE);
    return this;
}
/** @return {HTMLElement} */
HTMLElement.prototype.show = function () {
    this.setAttribute(HTMLElement.VISIBLE, '');
    return this;
}

const dialogHandler = {
    /** @type {HTMLElement} */
    $dialog: document.getElementById('dialog'),
    /**
     * @param {{title?: String, content?: String, buttons?: {caption?: String, onclick?: function(HTMLElement?)}[]}} args
     */
    showModal: (args) => {
        const $modal = document.createElement('div');
        $modal.classList.add('modal');
        const $title = document.createElement('div');
        $title.classList.add('title');
        $title.innerText = args.title;
        const $content = document.createElement('div');
        $content.classList.add('content');
        $content.innerText = args.content;
        const $buttonContainer = document.createElement('div');
        $buttonContainer.classList.add('button-container');
        if (args.buttons != null && args.buttons.length > 0) {
            for (const button of args.buttons) {
                const $button = document.createElement('button');
                $button.classList.add('button');
                $button.setAttribute('type', 'button');
                $button.innerText = button.caption;
                if (typeof button.onclick === 'function') {
                    $button.addEventListener('click', () => button.onclick());
                }
                $buttonContainer.append($button);
            }
        }
        $modal.append($title, $content, $buttonContainer);
        dialogHandler.$dialog.append($modal);
        setTimeout(() => dialogHandler.$dialog.show(), 100);
    },
    /**
     * @param {String} title
     * @param {String} content
     * @param {{caption?: string, onclick?: function(HTMLElement?)||undefined}} args
     */
    simpleYesModal: (title, content, args = {caption: "확인", onclick: undefined}) => {
        dialogHandler.showModal({
            title: title,
            content: content,
            buttons: [{
                caption: args?.caption ?? "확인",
                onclick: () => {
                    dialogHandler.$dialog.hide();
                    if (typeof args?.onclick === 'function') {
                        args?.onclick();
                    }
                    setTimeout(() => dialogHandler.$dialog.querySelector(':scope > .modal').remove(), 500);
                }
            }],
        });
    },
    simpleYesNoModal: (title, content, args = [{
        caption: "취소", onclick: undefined
    }, {
        caption: "확인", onclick: undefined
    }]) => {
        dialogHandler.showModal({
            title: title,
            content: content,
            buttons: [{
                caption: args[0]?.caption ?? "취소",
                onclick: () => {
                    dialogHandler.$dialog.hide();
                    if (typeof args[0]?.onclick === 'function') {
                        args[0]?.onclick();
                    }
                    setTimeout(() => dialogHandler.$dialog.querySelector(':scope > .modal').remove(), 500);
                }
            }, {
                caption: args[1]?.caption ?? "확인",
                onclick: () => {
                    dialogHandler.$dialog.hide();
                    if (typeof args[1]?.onclick === 'function') {
                        args[1]?.onclick();
                    }
                    setTimeout(() => dialogHandler.$dialog.querySelector(':scope > .modal').remove(), 500);
                }
            }],
        });
    },
}