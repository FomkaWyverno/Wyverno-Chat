const collapse_button = document.querySelector('.list-control-panel__collapse');
const maximize_minimize_button = document.querySelector('.list-control-panel__maximize-minimize');
const close_button = document.querySelector('.list-control-panel__close');

collapse_button.addEventListener('click', () => { // Сворачиваем окно
    console.log('click collapse')
    ipcRenderer.send('window.collapse')
});
maximize_minimize_button.addEventListener('click', () => { // Маштабируем или минимизируем
    console.log('click maximize_minimize');
    ipcRenderer.send('window.maximize-minimize');
});
close_button.addEventListener('click', () => { // Закрываем окно
    console.log('click close');
    ipcRenderer.send('window.close')
});

ipcRenderer.on('window.maximize',() => 
{
    console.log('Maximize')
});

ipcRenderer.on('window.minimize',() =>
{
    console.log('Minimize')
    maximize_minimize_button.firstElementChild.classList.add('minimize')
});