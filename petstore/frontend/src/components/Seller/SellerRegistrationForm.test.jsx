import { render, screen, fireEvent, waitFor } from '@testing-library/react';
import userEvent from '@testing-library/user-event';
import SellerRegistrationForm from '../components/Seller/SellerRegistrationForm';

describe('SellerRegistrationForm', () => {
  const mockOnSubmit = jest.fn();

  beforeEach(() => {
    jest.clearAllMocks();
  });

  test('renders form with all required fields', () => {
    render(<SellerRegistrationForm onSubmit={mockOnSubmit} />);

    expect(screen.getByText('Tell Us About Your Business')).toBeInTheDocument();
    expect(screen.getByLabelText(/Business Name/)).toBeInTheDocument();
    expect(screen.getByLabelText(/Business Description/)).toBeInTheDocument();
    expect(screen.getByLabelText(/Return & Refund Policy/)).toBeInTheDocument();
    expect(screen.getByText('Continue to Email Verification')).toBeInTheDocument();
  });

  test('shows character count for each field', () => {
    render(<SellerRegistrationForm onSubmit={mockOnSubmit} />);

    expect(screen.getByText('0/100 characters')).toBeInTheDocument(); // Business name
    expect(screen.getByText('0/1000 characters')).toBeInTheDocument(); // Bio
    expect(screen.getByText('0/2000 characters')).toBeInTheDocument(); // Return policy
  });

  test('validates required fields on submit', async () => {
    render(<SellerRegistrationForm onSubmit={mockOnSubmit} />);

    const submitButton = screen.getByText('Continue to Email Verification');
    fireEvent.click(submitButton);

    await waitFor(() => {
      expect(screen.getByText('Business name is required')).toBeInTheDocument();
      expect(screen.getByText('Business description is required')).toBeInTheDocument();
      expect(screen.getByText('Return policy is required')).toBeInTheDocument();
    });

    expect(mockOnSubmit).not.toHaveBeenCalled();
  });

  test('validates business name field', async () => {
    render(<SellerRegistrationForm onSubmit={mockOnSubmit} />);

    const businessNameInput = screen.getByPlaceholderText('Enter your business name');
    const submitButton = screen.getByText('Continue to Email Verification');

    // Test too short
    await userEvent.type(businessNameInput, 'AB');
    fireEvent.blur(businessNameInput);

    await waitFor(() => {
      expect(screen.getByText('Business name must be at least 3 characters')).toBeInTheDocument();
    });

    // Test valid
    await userEvent.clear(businessNameInput);
    await userEvent.type(businessNameInput, 'Valid Business');

    // Fill other required fields
    const bioInput = screen.getByPlaceholderText(/Describe your business/);
    const returnPolicyInput = screen.getByPlaceholderText(/Outline your return/);

    await userEvent.type(bioInput, 'This is a valid business description');
    await userEvent.type(returnPolicyInput, 'This is a valid return policy that is long enough');

    fireEvent.click(submitButton);

    await waitFor(() => {
      expect(mockOnSubmit).toHaveBeenCalledWith(
        expect.objectContaining({
          businessName: 'Valid Business',
        })
      );
    });
  });

  test('validates bio field', async () => {
    render(<SellerRegistrationForm onSubmit={mockOnSubmit} />);

    const bioInput = screen.getByPlaceholderText(/Describe your business/);
    const submitButton = screen.getByText('Continue to Email Verification');

    // Test too short
    await userEvent.type(bioInput, 'Short');
    fireEvent.blur(bioInput);

    await waitFor(() => {
      expect(screen.getByText('Description must be at least 10 characters')).toBeInTheDocument();
    });

    // Test valid
    await userEvent.clear(bioInput);
    await userEvent.type(bioInput, 'This is a valid business description that is long enough');

    // Fill other required fields
    const businessNameInput = screen.getByPlaceholderText('Enter your business name');
    const returnPolicyInput = screen.getByPlaceholderText(/Outline your return/);

    await userEvent.type(businessNameInput, 'Valid Business');
    await userEvent.type(returnPolicyInput, 'This is a valid return policy');

    fireEvent.click(submitButton);

    await waitFor(() => {
      expect(mockOnSubmit).toHaveBeenCalledWith(
        expect.objectContaining({
          bio: 'This is a valid business description that is long enough',
        })
      );
    });
  });

  test('validates return policy field', async () => {
    render(<SellerRegistrationForm onSubmit={mockOnSubmit} />);

    const returnPolicyInput = screen.getByPlaceholderText(/Outline your return/);
    const submitButton = screen.getByText('Continue to Email Verification');

    // Test too short
    await userEvent.type(returnPolicyInput, 'Short policy');
    fireEvent.blur(returnPolicyInput);

    await waitFor(() => {
      expect(screen.getByText('Return policy must be at least 20 characters')).toBeInTheDocument();
    });

    // Test valid
    await userEvent.clear(returnPolicyInput);
    await userEvent.type(returnPolicyInput, 'This is a valid return policy that is long enough for the requirements');

    // Fill other required fields
    const businessNameInput = screen.getByPlaceholderText('Enter your business name');
    const bioInput = screen.getByPlaceholderText(/Describe your business/);

    await userEvent.type(businessNameInput, 'Valid Business');
    await userEvent.type(bioInput, 'This is a valid business description');

    fireEvent.click(submitButton);

    await waitFor(() => {
      expect(mockOnSubmit).toHaveBeenCalledWith(
        expect.objectContaining({
          returnPolicy: 'This is a valid return policy that is long enough for the requirements',
        })
      );
    });
  });

  test('updates character count as user types', async () => {
    render(<SellerRegistrationForm onSubmit={mockOnSubmit} />);

    const businessNameInput = screen.getByPlaceholderText('Enter your business name');

    // Initial count
    expect(screen.getByText('0/100 characters')).toBeInTheDocument();

    // Type 5 characters
    await userEvent.type(businessNameInput, 'Hello');

    await waitFor(() => {
      expect(screen.getByText('5/100 characters')).toBeInTheDocument();
    });

    // Type 5 more characters
    await userEvent.type(businessNameInput, ' World');

    await waitFor(() => {
      expect(screen.getByText('11/100 characters')).toBeInTheDocument();
    });
  });

  test('disables submit button when loading', () => {
    const { rerender } = render(
      <SellerRegistrationForm onSubmit={mockOnSubmit} isLoading={false} />
    );

    let submitButton = screen.getByText('Continue to Email Verification');
    expect(submitButton).not.toBeDisabled();

    rerender(
      <SellerRegistrationForm onSubmit={mockOnSubmit} isLoading={true} />
    );

    submitButton = screen.getByText('Creating Seller Account...');
    expect(submitButton).toBeDisabled();
  });

  test('trims whitespace from input before submission', async () => {
    render(<SellerRegistrationForm onSubmit={mockOnSubmit} />);

    const businessNameInput = screen.getByPlaceholderText('Enter your business name');
    const bioInput = screen.getByPlaceholderText(/Describe your business/);
    const returnPolicyInput = screen.getByPlaceholderText(/Outline your return/);
    const submitButton = screen.getByText('Continue to Email Verification');

    await userEvent.type(businessNameInput, '  Business with spaces  ');
    await userEvent.type(bioInput, '  Valid description with spaces  ');
    await userEvent.type(returnPolicyInput, '  Valid return policy with spaces  ');

    fireEvent.click(submitButton);

    await waitFor(() => {
      expect(mockOnSubmit).toHaveBeenCalledWith(
        expect.objectContaining({
          businessName: 'Business with spaces',
          bio: 'Valid description with spaces',
          returnPolicy: 'Valid return policy with spaces',
        })
      );
    });
  });

  test('marks fields as touched on blur', async () => {
    render(<SellerRegistrationForm onSubmit={mockOnSubmit} />);

    const businessNameInput = screen.getByPlaceholderText('Enter your business name');

    // Initially no error shown
    expect(screen.queryByText('Business name is required')).not.toBeInTheDocument();

    // Blur empty field
    fireEvent.blur(businessNameInput);

    // Now error should be shown
    await waitFor(() => {
      expect(screen.getByText('Business name is required')).toBeInTheDocument();
    });
  });

  test('clears error when user fixes field after blur', async () => {
    render(<SellerRegistrationForm onSubmit={mockOnSubmit} />);

    const businessNameInput = screen.getByPlaceholderText('Enter your business name');

    // Create error
    fireEvent.blur(businessNameInput);

    await waitFor(() => {
      expect(screen.getByText('Business name is required')).toBeInTheDocument();
    });

    // Fix field
    await userEvent.type(businessNameInput, 'Valid Business Name');

    // Error should clear
    await waitFor(() => {
      expect(screen.queryByText('Business name is required')).not.toBeInTheDocument();
    });
  });

  test('successfully submits valid form data', async () => {
    render(<SellerRegistrationForm onSubmit={mockOnSubmit} />);

    const businessNameInput = screen.getByPlaceholderText('Enter your business name');
    const bioInput = screen.getByPlaceholderText(/Describe your business/);
    const returnPolicyInput = screen.getByPlaceholderText(/Outline your return/);
    const submitButton = screen.getByText('Continue to Email Verification');

    await userEvent.type(businessNameInput, 'Pet Paradise');
    await userEvent.type(bioInput, 'We specialize in rare and exotic pets with proper certification');
    await userEvent.type(returnPolicyInput, 'All pets come with a 30-day health guarantee. If your pet gets sick within 30 days, we will provide a full refund or replacement.');

    fireEvent.click(submitButton);

    await waitFor(() => {
      expect(mockOnSubmit).toHaveBeenCalledTimes(1);
      expect(mockOnSubmit).toHaveBeenCalledWith({
        businessName: 'Pet Paradise',
        bio: 'We specialize in rare and exotic pets with proper certification',
        returnPolicy: 'All pets come with a 30-day health guarantee. If your pet gets sick within 30 days, we will provide a full refund or replacement.',
      });
    });
  });
});
