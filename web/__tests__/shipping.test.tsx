import "@testing-library/jest-dom";
import { render, screen } from "@testing-library/react";
import Shipping from "@/app/[locale]/shipping/page";

describe("Home", () => {
  it("renders a heading", () => {
    render(<Shipping />);

    const heading = screen.getByRole("heading", { level: 1 });

    expect(heading).toBeInTheDocument();
  });
});
