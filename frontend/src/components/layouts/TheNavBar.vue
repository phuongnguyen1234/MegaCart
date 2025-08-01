<template>
  <nav
    class="sticky top-[70px] z-40 flex text-white bg-[linear-gradient(135deg,_#1E88E5,_#1565C0)]"
  >
    <div class="flex-1 text-center relative group">
      <router-link
        to="/thuc-pham"
        class="select-none block py-[15px] px-[20px] text-white no-underline whitespace-nowrap hover:bg-[linear-gradient(135deg,_#42A5F5,_#1E88E5)] hover:text-[#E3F2FD]"
        >Thực phẩm</router-link
      >
      <div
        class="absolute left-0 top-full w-full hidden group-hover:flex flex-col bg-[#1976D2] shadow-md z-50"
      >
        <router-link
          to="/thuc-pham/banh-mi"
          class="select-none px-4 py-2 text-white no-underline text-left hover:bg-[linear-gradient(135deg,_#90CAF9,_#42A5F5)]"
          >Bánh mì</router-link
        >
        <router-link
          to="/thuc-pham/bo-sua"
          class="select-none px-4 py-2 text-white no-underline text-left hover:bg-[linear-gradient(135deg,_#90CAF9,_#42A5F5)]"
          >Bơ sữa</router-link
        >
      </div>
    </div>
    <div class="flex-1 text-center relative group">
      <router-link
        to="/nha-cua-va-noi-that"
        class="select-none block py-[15px] px-[20px] text-white no-underline whitespace-nowrap hover:bg-[linear-gradient(135deg,_#42A5F5,_#1E88E5)] hover:text-[#E3F2FD]"
        >Nhà cửa & Nội thất</router-link
      >
      <div
        class="absolute left-0 top-full w-full hidden group-hover:flex flex-col bg-[#1976D2] shadow-md z-50"
      >
        <router-link
          to="/nha-cua-va-noi-that/noi-that"
          class="select-none px-4 py-2 text-white no-underline text-left hover:bg-[linear-gradient(135deg,_#90CAF9,_#42A5F5)]"
          >Nội thất</router-link
        >
        <router-link
          to="/nha-cua-va-noi-that/thiet-bi"
          class="select-none px-4 py-2 text-white no-underline text-left hover:bg-[linear-gradient(135deg,_#90CAF9,_#42A5F5)]"
          >Thiết bị</router-link
        >
      </div>
    </div>
    <div class="flex-1 text-center relative group">
      <router-link
        to="/thoi-trang"
        class="select-none block py-[15px] px-[20px] text-white no-underline whitespace-nowrap hover:bg-[linear-gradient(135deg,_#42A5F5,_#1E88E5)] hover:text-[#E3F2FD]"
        >Thời trang</router-link
      >
      <div
        class="absolute left-0 top-full w-full hidden group-hover:flex flex-col bg-[#1976D2] shadow-md z-50"
      >
        <router-link
          to="/thoi-trang/ao-so-mi"
          class="select-none px-4 py-2 text-white no-underline text-left hover:bg-[linear-gradient(135deg,_#90CAF9,_#42A5F5)]"
          >Áo sơ mi</router-link
        >
        <router-link
          to="/thoi-trang/quan-jeans"
          class="select-none px-4 py-2 text-white no-underline text-left hover:bg-[linear-gradient(135deg,_#90CAF9,_#42A5F5)]"
          >Quần jeans</router-link
        >
      </div>
    </div>
    <div class="flex-1 text-center relative group">
      <router-link
        to="/dien-tu"
        class="select-none block py-[15px] px-[20px] text-white no-underline whitespace-nowrap hover:bg-[linear-gradient(135deg,_#42A5F5,_#1E88E5)] hover:text-[#E3F2FD]"
        >Điện tử</router-link
      >
      <div
        class="absolute left-0 top-full w-full hidden group-hover:flex flex-col bg-[#1976D2] shadow-md z-50"
      >
        <router-link
          to="/dien-tu/smartphone"
          class="select-none px-4 py-2 text-white no-underline text-left hover:bg-[linear-gradient(135deg,_#90CAF9,_#42A5F5)]"
          >Smartphone</router-link
        >
        <router-link
          to="/dien-tu/laptop"
          class="select-none px-4 py-2 text-white no-underline text-left hover:bg-[linear-gradient(135deg,_#90CAF9,_#42A5F5)]"
          >Laptop</router-link
        >
      </div>
    </div>
    <div class="flex-1 text-center relative group">
      <router-link
        to="/me-va-be"
        class="select-none block py-[15px] px-[20px] text-white no-underline whitespace-nowrap hover:bg-[linear-gradient(135deg,_#42A5F5,_#1E88E5)] hover:text-[#E3F2FD]"
        >Mẹ và bé</router-link
      >
      <div
        class="absolute left-0 top-full w-full hidden group-hover:flex flex-col bg-[#1976D2] shadow-md z-50"
      >
        <router-link
          to="/me-va-be/do-choi"
          class="select-none px-4 py-2 text-white no-underline text-left hover:bg-[linear-gradient(135deg,_#90CAF9,_#42A5F5)]"
          >Đồ chơi</router-link
        >
        <router-link
          to="/me-va-be/do-so-sinh"
          class="select-none px-4 py-2 text-white no-underline text-left hover:bg-[linear-gradient(135deg,_#90CAF9,_#42A5F5)]"
          >Đồ sơ sinh</router-link
        >
      </div>
    </div>
  </nav>
</template>
