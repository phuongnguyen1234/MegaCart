<template>
  <nav
    class="sticky top-[70px] z-40 flex text-white bg-[linear-gradient(135deg,_#1E88E5,_#1565C0)]"
  >
    <div class="flex-1 text-center relative group">
      <router-link
        to="/danh-muc/Tat-ca"
        class="select-none block py-[15px] px-[20px] text-white no-underline whitespace-nowrap hover:bg-[linear-gradient(135deg,_#42A5F5,_#1E88E5)] hover:text-[#E3F2FD]"
        >Tất cả danh mục</router-link
      >
    </div>
    <div class="flex-1 text-center relative group">
      <router-link
        to="/danh-muc/Thuc-pham"
        class="select-none block py-[15px] px-[20px] text-white no-underline whitespace-nowrap hover:bg-[linear-gradient(135deg,_#42A5F5,_#1E88E5)] hover:text-[#E3F2FD]"
        >Thực phẩm</router-link
      >
      <div
        class="absolute left-0 top-full w-full hidden group-hover:flex flex-col bg-[#1976D2] shadow-md z-50"
      >
        <router-link
          to="/danh-muc/Banh-mi"
          class="select-none px-4 py-2 text-white no-underline text-left hover:bg-[linear-gradient(135deg,_#90CAF9,_#42A5F5)]"
          >Bánh mì</router-link
        >
        <router-link
          to="/danh-muc/Sua"
          class="select-none px-4 py-2 text-white no-underline text-left hover:bg-[linear-gradient(135deg,_#90CAF9,_#42A5F5)]"
          >Sữa</router-link
        >
      </div>
    </div>
    <div class="flex-1 text-center relative group">
      <router-link
        to="/danh-muc/Nha-cua-va-Noi-that"
        class="select-none block py-[15px] px-[20px] text-white no-underline whitespace-nowrap hover:bg-[linear-gradient(135deg,_#42A5F5,_#1E88E5)] hover:text-[#E3F2FD]"
        >Nhà cửa & Nội thất</router-link
      >
      <div
        class="absolute left-0 top-full w-full hidden group-hover:flex flex-col bg-[#1976D2] shadow-md z-50"
      >
        <router-link
          to="/danh-muc/Noi-that"
          class="select-none px-4 py-2 text-white no-underline text-left hover:bg-[linear-gradient(135deg,_#90CAF9,_#42A5F5)]"
          >Nội thất</router-link
        >
        <router-link
          to="/danh-muc/Thiet-bi"
          class="select-none px-4 py-2 text-white no-underline text-left hover:bg-[linear-gradient(135deg,_#90CAF9,_#42A5F5)]"
          >Thiết bị</router-link
        >
      </div>
    </div>
    <div class="flex-1 text-center relative group">
      <router-link
        to="/danh-muc/Thoi-trang"
        class="select-none block py-[15px] px-[20px] text-white no-underline whitespace-nowrap hover:bg-[linear-gradient(135deg,_#42A5F5,_#1E88E5)] hover:text-[#E3F2FD]"
        >Thời trang</router-link
      >
      <div
        class="absolute left-0 top-full w-full hidden group-hover:flex flex-col bg-[#1976D2] shadow-md z-50"
      >
        <router-link
          to="/danh-muc/Ao-so-mi"
          class="select-none px-4 py-2 text-white no-underline text-left hover:bg-[linear-gradient(135deg,_#90CAF9,_#42A5F5)]"
          >Áo sơ mi</router-link
        >
        <router-link
          to="/danh-muc/Quan"
          class="select-none px-4 py-2 text-white no-underline text-left hover:bg-[linear-gradient(135deg,_#90CAF9,_#42A5F5)]"
          >Quần</router-link
        >
      </div>
    </div>
    <div class="flex-1 text-center relative group">
      <router-link
        to="/danh-muc/Khac"
        class="select-none block py-[15px] px-[20px] text-white no-underline whitespace-nowrap hover:bg-[linear-gradient(135deg,_#42A5F5,_#1E88E5)] hover:text-[#E3F2FD]"
        >Khác</router-link
      >
    </div>
  </nav>
</template>
