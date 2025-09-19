import Link from "next/link";

export default function Home() {
  return (
    <>
      <div className="flex flex-col">
        <h1>Home</h1>
        <Link href="/about">About</Link>
        <Link href="/shipping">Shipping</Link>
        <Link href="/terms-of-use">Terms of use</Link>
      </div>
    </>
  );
}
