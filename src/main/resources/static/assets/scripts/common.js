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

/** @type {HTMLElement} */
const $dialog = document.getElementById('dialog');

/**
 * @param {{title?: String, content?: String, buttons?: {caption?: String, onclick?: function(HTMLElement?)}[]}} args
 */
function showModal(args) {
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
    $dialog.append($modal);
    setTimeout(() => $modal.show(), 100);
}