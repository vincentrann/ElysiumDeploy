function toggleProducts(button) {
    const productRow = button.closest('tr').nextElementSibling;
    productRow.classList.toggle('hidden');
    button.textContent = productRow.classList.contains('hidden') ? 'Expand' : 'Hide';
}