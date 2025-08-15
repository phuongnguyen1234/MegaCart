<template>
  <nav
    class="sticky z-40 flex text-white bg-[linear-gradient(135deg,_#1E88E5,_#1565C0)]"
  >
    <!-- Tất cả danh mục -->
    <div class="flex-1 text-center relative group">
      <div
        class="select-none block py-[15px] px-[20px] text-white no-underline whitespace-nowrap cursor-pointer hover:bg-[linear-gradient(135deg,_#42A5F5,_#1E88E5)] hover:text-[#E3F2FD]"
      >
        Tất cả danh mục
      </div>
      <div
        class="absolute left-0 top-full w-full hidden group-hover:flex flex-col bg-[#1976D2] shadow-md z-50"
      >
        <!-- Nội dung danh mục sẽ load từ API -->
      </div>
    </div>

    <!-- Các danh mục cha khác -->
    <div
      v-for="parent in categories"
      :key="parent.slug"
      class="flex-1 text-center relative group"
    >
      <router-link
        :to="parent.link"
        class="select-none block py-[15px] px-[20px] text-white no-underline whitespace-nowrap hover:bg-[linear-gradient(135deg,_#42A5F5,_#1E88E5)] hover:text-[#E3F2FD]"
      >
        {{ parent.name }}
      </router-link>

      <div
        class="absolute left-0 top-full w-full hidden group-hover:flex flex-col bg-[#1976D2] shadow-md z-50"
      >
        <!-- Grid 8x2 -->
        <div class="grid grid-cols-2">
          <router-link
            v-for="child in parent.children.slice(0, 12)"
            :key="child.slug"
            :to="child.link"
            class="select-none px-4 py-2 text-white no-underline text-left hover:bg-[linear-gradient(135deg,_#90CAF9,_#42A5F5)]"
          >
            {{ child.name }}
          </router-link>
        </div>
        <!-- Xem thêm -->
        <router-link
          v-if="parent.children.length > 12"
          :to="parent.link"
          class="block px-4 py-2 text-center font-semibold text-[#BBDEFB] hover:text-white hover:bg-[linear-gradient(135deg,_#42A5F5,_#1E88E5)]"
        >
          Xem thêm
        </router-link>
      </div>
    </div>
  </nav>
</template>

<script setup>
const categories = [
  {
    name: "Thực phẩm",
    slug: "thuc-pham",
    link: "/thuc-pham",
    children: [
      { name: "Bánh mì", slug: "banh-mi", link: "/thuc-pham/banh-mi" },
      { name: "Bơ sữa", slug: "bo-sua", link: "/thuc-pham/bo-sua" },
      { name: "Rau củ", slug: "rau-cu", link: "/thuc-pham/rau-cu" },
      { name: "Thịt cá", slug: "thit-ca", link: "/thuc-pham/thit-ca" },
      { name: "Gia vị", slug: "gia-vi", link: "/thuc-pham/gia-vi" },
      { name: "Hải sản", slug: "hai-san", link: "/thuc-pham/hai-san" },
      { name: "Trái cây", slug: "trai-cay", link: "/thuc-pham/trai-cay" },
      { name: "Ngũ cốc", slug: "ngu-coc", link: "/thuc-pham/ngu-coc" },
      { name: "Đồ hộp", slug: "do-hop", link: "/thuc-pham/do-hop" },
      { name: "Mì gói", slug: "mi-goi", link: "/thuc-pham/mi-goi" },
      { name: "Nước ngọt", slug: "nuoc-ngot", link: "/thuc-pham/nuoc-ngot" },
      { name: "Nước suối", slug: "nuoc-suoi", link: "/thuc-pham/nuoc-suoi" },
      { name: "Trà", slug: "tra", link: "/thuc-pham/tra" }, //muc 13
    ],
  },
  {
    name: "Nội - Ngoại thất",
    slug: "nha-cua-va-noi-that",
    link: "/nha-cua-va-noi-that",
    children: [
      {
        name: "Nội thất phòng khách",
        slug: "phong-khach",
        link: "/nha-cua-va-noi-that/phong-khach",
      },
      {
        name: "Nội thất phòng ngủ",
        slug: "phong-ngu",
        link: "/nha-cua-va-noi-that/phong-ngu",
      },
      {
        name: "Nhà bếp",
        slug: "nha-bep",
        link: "/nha-cua-va-noi-that/nha-bep",
      },
      {
        name: "Bàn ghế",
        slug: "ban-ghe",
        link: "/nha-cua-va-noi-that/ban-ghe",
      },
      { name: "Tủ kệ", slug: "tu-ke", link: "/nha-cua-va-noi-that/tu-ke" },
      {
        name: "Thiết bị điện",
        slug: "thiet-bi-dien",
        link: "/nha-cua-va-noi-that/thiet-bi-dien",
      },
      {
        name: "Ngoại thất sân vườn",
        slug: "san-vuon",
        link: "/nha-cua-va-noi-that/san-vuon",
      },
      {
        name: "Đèn trang trí",
        slug: "den-trang-tri",
        link: "/nha-cua-va-noi-that/den-trang-tri",
      },
      {
        name: "Vật liệu xây dựng",
        slug: "vat-lieu",
        link: "/nha-cua-va-noi-that/vat-lieu",
      },
      { name: "Cửa & cửa sổ", slug: "cua", link: "/nha-cua-va-noi-that/cua" },
      { name: "Sàn gỗ", slug: "san-go", link: "/nha-cua-va-noi-that/san-go" },
    ],
  },
  {
    name: "Điện tử",
    slug: "dien-tu",
    link: "/dien-tu",
    children: [
      { name: "Smartphone", slug: "smartphone", link: "/dien-tu/smartphone" },
      { name: "Laptop", slug: "laptop", link: "/dien-tu/laptop" },
      { name: "Tablet", slug: "tablet", link: "/dien-tu/tablet" },
      { name: "Tai nghe", slug: "tai-nghe", link: "/dien-tu/tai-nghe" },
    ],
  },
];
</script>
