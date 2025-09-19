import "@testing-library/jest-dom";
import { render, screen } from "@testing-library/react";
import TermsOfUse from "@/app/[locale]/terms-of-use/page";

describe("Terms-of-use", () => {
  it("renders a heading", () => {
    render(<TermsOfUse />);

    const heading = screen.getByRole("heading", { level: 1 });

    expect(heading).toBeInTheDocument();
  });
});
