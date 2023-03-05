let getCartCount = function (customer) {
  fetch(`http://localhost:8081/farmfoods/item/cartcount/` + customer)
    .then(function (ajaxResponse) {
      return ajaxResponse.json();
    })
    .then(function (numberOfItemsInCart) {
      console.log(numberOfItemsInCart);
      document.getElementById("nav-no-of-items-cart").textContent =
        "(" + numberOfItemsInCart + ")";
    });
};

getCartCount(cartObject.customerId);
